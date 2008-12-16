/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mongodb.driver.impl;

import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.MongoDBIOException;
import org.mongodb.driver.util.BSONObject;
import org.mongodb.driver.impl.msg.DBMessageHeader;
import org.mongodb.driver.impl.msg.DBKillCursorsMessage;
import org.mongodb.driver.impl.msg.DBGetMoreMessage;

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

/**
 *   Represents a response message from the DB.  Currently these will be sent
 *   only in response to either a Query or a GetMore.
 *
 */
class DBCursorImpl implements DBCursor {

    protected Queue<MongoDoc> _objects = new LinkedList<MongoDoc>();

    private static final int RESPONSE_HEADER_SIZE = 20;

    protected DBMessageHeader msgHeader = new DBMessageHeader();  // TODO - move this to DBMessage

    protected ByteBuffer _headerBuf = ByteBuffer.allocate(RESPONSE_HEADER_SIZE);
    protected ByteBuffer _sizeBuf = ByteBuffer.allocate(4);

    protected DBImpl _myDB;
    protected String _collection;

    protected int _resultFlags;   // status of response : ok = 0, problem = 1
    protected long _cursorID;     // identifier of cursor for GetMore requests
    protected int _startingFrom;  // starting point in cursor for this response
    protected int _nReturned;     // number returned. 0 == infinity

    protected int _nRemaining;    // number of objects remaining to be read from db connection

    protected boolean _closed = false;


    /**
     *  Create a new DBCursor, including reading the objects off the wire - this allows for
     *  result-sets to be inteleaved on the same connection.
     * 
     * @param db db this cursor is associated with
     * @param collection collection this cursor is working over
     * @throws MongoDBException on network error
     */
    public DBCursorImpl(DBImpl db, String collection) throws MongoDBException {
        _headerBuf.order(ByteOrder.LITTLE_ENDIAN);
        _sizeBuf.order(ByteOrder.LITTLE_ENDIAN);
        _myDB = db;
        _collection = collection;

        readAll();
    }

    protected void readAll() throws  MongoDBException {
        readMessageHeader();
        readResponseHeader();
        readObjectsOffWire();
    }

    protected void readObjectsOffWire() throws MongoDBException {

        MongoDoc doc;

        while((doc = getNextObjectOnWire()) != null) {
            _objects.offer(doc);
        }
    }

    protected void readMessageHeader() throws MongoDBException {

        try {
            InputStream is = _myDB._sock.getInputStream();

            DBMessageHeader.readHeader(is);
        }
        catch(IOException ioe) {
            throw new MongoDBException(ioe);
        }
    }

    protected void readResponseHeader() throws MongoDBException  {

        try {
            InputStream is = _myDB._sock.getInputStream();

            _headerBuf.position(0);
            
            int i = is.read(_headerBuf.array(), 0, RESPONSE_HEADER_SIZE);

            if (i != RESPONSE_HEADER_SIZE) {
                throw new IOException("Short read for DB response header");
            }

            _resultFlags = _headerBuf.getInt();
            _cursorID = _headerBuf.getLong();
            _startingFrom = _headerBuf.getInt();
            _nReturned = _headerBuf.getInt();

            _nRemaining = _nReturned;
        }
        catch(IOException ioe) {
            throw new MongoDBException(ioe);
        }
    }

    /**
     *  Closes the cursor, releasing any server-side cursors and
     *  dumping objects and buffers
     */
    public void close() throws MongoDBException {

        if (_cursorID != 0) {
            _myDB.sendToDB(new DBKillCursorsMessage(_cursorID));
        }
        
        _objects.clear();

        _closed = true;
    }

    public boolean hasMoreElements() {

        if (getNumRemaining() == 0) {
            try {
                refillViaGetMore();
            }
            catch (MongoDBException e) {
                e.printStackTrace();
            }
        }

        return getNumRemaining() > 0;
    }

    public Object nextElement() {
        try {
            return getNextObject();
        } catch (MongoDBException e) {
            return null;
        }
    }

    public Iterator<MongoDoc> iterator() {

        return new Iterator<MongoDoc>() {
            public boolean hasNext() {
                return getNumRemaining() != 0;
            }

            public MongoDoc next() {
                try {
                    return getNextObject();
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void remove() {
                try {
                    getNextObject();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public MongoDoc getNextObject() throws MongoDBException {

        if (getNumRemaining() == 0) {
            refillViaGetMore();
        }

        return _objects.poll();
    }

    public int getNumReturned() {
        return _nReturned;
    }

    public int getNumRemaining() {
        if (_objects.size() == 0) {
            try {
                refillViaGetMore();
            }
            catch (MongoDBException e) {
                e.printStackTrace();
            }
        }

        return _objects.size();
    }

    protected MongoDoc getNextObjectOnWire() throws MongoDBException {

        if (_nRemaining == 0) {

            // if we have a non-zero cursor, there are more to fetch, so do a
            // GetMore operation, but don't do it here - do it when someone pulls an
            // object out of the cache and it's empty

            return null;
        }
        else {
            return getObjectFromStream();
        }
    }

    private void refillViaGetMore() throws MongoDBException {

        /*
         *  if we don't have a cursor, bail - we're done
         */
        if (_cursorID == 0) {
            return;
        }

        // TODO - this is russian routlette - there's no predicting the size of the return
        // for a getmore right now.  The first response to a query is limited to not hurt those
        // that don't limit their requests, and the following after that get much bigger.

        _myDB.sendToDB(new DBGetMoreMessage(_myDB.getName(), _collection, _cursorID ));

        readAll();        
    }

    
    private MongoDoc getObjectFromStream() throws MongoDBException {

        _sizeBuf.rewind();
        
        // read the size of the object and keep so we can patch back into the buffer

        try {
            byte[] bufInternal = _sizeBuf.array();
            int i = readStream(bufInternal, 0, 4);

            if (i != 4) {
                throw new IOException("Short read for DB object size read");
            }

            int size = _sizeBuf.getInt();

            // read the rest of the object, patch the size back in, and then deserialize to a mongodoc

            byte[] buffer = new byte[size];
            i = readStream(buffer, 4, size - 4);

            assert(i == size-4);

            System.arraycopy(bufInternal, 0, buffer, 0, 4);

            _nRemaining--;

            BSONObject o = new BSONObject();
            return o.deserialize(buffer);
        } catch (IOException e) {
            throw new MongoDBIOException("IO Exception : ", e);
        }
    }

    /**
     *  Reads bytes from the database connection
     * 
     * @param arr  array to write data to
     * @param start start position in buffer to write to
     * @param len number of bytes to read
     * @return number of bytes read
     * @throws IOException in case of problem
     */
    protected int readStream(byte[] arr, int start, int len) throws IOException {

        InputStream is = _myDB._sock.getInputStream();

        return  is.read(arr, start, len);
    }

    public String toString() {
        return "DBResponse : flags=[" + _resultFlags + "]  cursorID=["
                    + _cursorID + "] start=[" + _startingFrom + "] nreturned=[" + _nReturned + "]";
    }
}
