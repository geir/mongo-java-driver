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

    protected ByteBuffer _headerBuf = ByteBuffer.allocate(RESPONSE_HEADER_SIZE);

    protected DBImpl _myDB;
    protected String _collection;

    protected int _resultFlags;   // status of response : ok = 0, problem = 1
    protected long _cursorID;     // identifier of cursor for GetMore requests
    protected int _startingFrom;  // starting point in cursor for this response
    protected int _nReturned;     // number returned. 0 == infinity

    protected int _nRemaining;    // number of objects remaining to be read from db connection

    /*
     *  mongo doesn't support limits - it has to be a client-side feature
     */
    protected final int _hardLimitTotal;     // number of objects requested as the limit
    protected final int _hardLimitReturned;  // number of objects returned to the client app

    protected boolean _closed = false;


    /**
     *  Create a new DBCursor with a limit set.
     *
     * @param db db this cursor is associated with
     * @param collection collection this cursor is working over
     * @param limit max number of objects that this cursor can deliver to a client.  <= 0 means no limit
     * @throws MongoDBException on network error
     */
    public DBCursorImpl(DBImpl db, String collection, int limit) throws MongoDBException {
        _headerBuf.order(ByteOrder.LITTLE_ENDIAN);
        
        _myDB = db;
        _collection = collection;

        if (limit <= 0) {
            limit = 0;
        }
        _hardLimitTotal = limit;
        _hardLimitReturned = 0;

        readAll();
    }


    /**
     *  Create a new DBCursor, including reading the objects off the wire - this allows for
     *  result-sets to be inteleaved on the same connection.
     * 
     * @param db db this cursor is associated with
     * @param collection collection this cursor is working over
     * @throws MongoDBException on network error
     */
    public DBCursorImpl(DBImpl db, String collection) throws MongoDBException {
        this(db,collection, 0);
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
            DBMessageHeader.readHeader(_myDB._socketChannel);
        }
        catch(IOException ioe) {
            throw new MongoDBException(ioe);
        }
    }

    protected void readResponseHeader() throws MongoDBException  {

        try {
            _headerBuf.clear();

            _myDB._socketChannel.read(_headerBuf);
            _headerBuf.flip();

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

        synchronized(_myDB._dbMonitor) {

            try {

                // read the size of the object and keep so we can patch back into the buffer
                
                ByteBuffer buf = DirectBufferTLS.getThreadLocal().getReadBuffer();

                buf.clear();
                buf.limit(4);

                long i = readStream(buf);

                assert(i == 4);

                int size = buf.getInt();

                // read the rest of the object, patch the size back in, and then deserialize to a mongodoc

                buf.position(4);
                buf.limit(size);

                i = readStream(buf);

                assert(i == size-4);

                _nRemaining--;

                BSONObject o = new BSONObject();
                byte[] buffer = new byte[size];
                buf.get(buffer);
                
                return o.deserialize(buffer);
            } catch (IOException e) {
                throw new MongoDBIOException("IO Exception : ", e);
            }
        }
    }
    
    /**
     *  Reads bytes from the database connection
     * 
     * @param buf  buffer to write into.  Buffer must have it's limit set for the expected read
     * @return number of bytes read
     * @throws IOException in case of problem
     */
    protected long readStream(ByteBuffer buf) throws IOException {

        long i =  _myDB._socketChannel.read(buf);
        buf.flip();

        return i;
    }

    public String toString() {
        return "DBResponse : flags=[" + _resultFlags + "]  cursorID=["
                    + _cursorID + "] start=[" + _startingFrom + "] nreturned=[" + _nReturned + "]";
    }
}
