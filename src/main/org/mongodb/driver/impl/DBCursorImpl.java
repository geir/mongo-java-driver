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
import org.mongodb.driver.MongoDBQueryException;
import org.mongodb.driver.impl.msg.DBKillCursorsMessage;
import org.mongodb.driver.impl.msg.DBGetMoreMessage;
import org.mongodb.driver.impl.msg.MessageType;
import org.mongodb.driver.impl.msg.DBQueryReplyMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
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

    protected DBImpl _myDB;
    protected String _collection;

    protected int _nRemaining = 0;    // number of objects remaining to be read from db connection

    DBQueryReplyMessage _msg = null;

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
    
    private void readAll() throws  MongoDBException {

        synchronized(_myDB._dbMonitor) {

            try {
                ByteBuffer buf = DirectBufferTLS.getThreadLocal().getReadBuffer();

                DBQueryReplyMessage.fillBufferWithHeaders(buf, _myDB._socketChannel);

                /*
                 * digest the message up to the objects... no nibbling..
                 */
                 _msg = new DBQueryReplyMessage(buf, false);

                /*
                 * now check that it really is a reply message, and that there are no errors
                 */

                if (_msg.getMessageType() != MessageType.OP_REPLY) {
                    throw new MongoDBException("Error : cursor received a [" + _msg.getMessageType() + "] response from server");
                }

                if (_msg.getFlags() != 0) {

                    MongoDoc md = DBQueryReplyMessage.readDocument(_myDB._socketChannel, buf, false);

                    throw new MongoDBQueryException("Error : cursor received am error response from server.  Flags = [ " + _msg.getFlags()
                            + "] Error msg :  " + md);
                }

                readObjectsOffWire();

                _nRemaining = _msg.getNumberReturned();
            }
            catch(IOException ioe) {
                throw new MongoDBException("Error filling buffer : ", ioe);
            }
        }
    }

    protected void readObjectsOffWire() throws MongoDBException {

        ByteBuffer buf = DirectBufferTLS.getThreadLocal().getReadBuffer();

        for (int i=0; i < _msg.getNumberReturned(); i++) {
            _objects.offer(DBQueryReplyMessage.readDocument(_myDB._socketChannel, buf, true));
        }
    }
    
    /**
     *  Closes the cursor, releasing any server-side cursors and
     *  dumping objects and buffers
     */
    public void close() throws MongoDBException {

        if (_msg.getCursorID() != 0) {
            _myDB.sendToDB(new DBKillCursorsMessage(_msg.getCursorID()));
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
        return _msg.getNumberReturned();
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


    private void refillViaGetMore() throws MongoDBException {

        /*
         *  if we don't have a cursor, bail - we're done
         */
        if (_msg.getCursorID() == 0) {
            return;
        }

        // TODO - this is russian routlette - there's no predicting the size of the return
        // for a getmore right now.  The first response to a query is limited to not hurt those
        // that don't limit their requests, and the following after that get much bigger.

        _myDB.sendToDB(new DBGetMoreMessage(_myDB.getName(), _collection, _msg.getCursorID() ));

        readAll();        
    }

    public String toString() {
        return "DBResponse : flags=[" + _msg.getFlags() + "]  cursorID=["
                    + _msg.getCursorID() + "] start=[" + _msg.getStartingFrom() + "] nreturned=[" + _msg.getNumberReturned() + "]";
    }
}
