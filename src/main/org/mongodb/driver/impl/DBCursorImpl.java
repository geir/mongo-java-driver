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

import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.Doc;
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
 *   Client-side implementation of the db-side cursor.  The cursor reads the wire and
 *   processes the response from a query to the database.  The DBCursorImpl will iteratively
 *   fetch more data via an OP_GET_MORE if needed.
 *
 *   TODO - this would make me happier if the CTOR took a DBQueryReplyMessage to start, rather than presume the wire is ready.
 *
 */
class DBCursorImpl implements DBCursor {

    protected Queue<Doc> _objects = new LinkedList<Doc>();

    protected DBImpl _myDB;
    protected String _collection;

    protected int _nRemaining = 0;    // number of objects remaining to be read from db connection

    DBQueryReplyMessage _msg = null;

    /*
     *  mongo doesn't support limits - it has to be a client-side feature
     */
    protected final int _hardLimit;     // number of objects requested as the limit
    protected int _objectsReturned;     // number of objects returned to the client app

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
        _hardLimit = limit;
        _objectsReturned = 0;

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

    /**
     *  Reads and processes a full DBQueryReplyMessage from the wire
     * 
     * @throws MongoDBException in case of problem
     */
    private void readAll() throws  MongoDBException {

        synchronized(_myDB._dbMonitor) {

            try {
                ByteBuffer buf = DirectBufferTLS.getThreadLocal().getReadBuffer();

                DBQueryReplyMessage.fillBufferWithHeaders(buf, _myDB._connection.getReadChannel());

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

                    Doc md = DBQueryReplyMessage.readDocument(_myDB._connection.getReadChannel(), buf, false);

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

    /**
     *  Reads all objects from the wire.  Assumes that the standard message header and the
     *  OP_REPLY header have already been read.
     * 
     * @throws MongoDBException in case of problem
     */
    protected void readObjectsOffWire() throws MongoDBException {

        ByteBuffer buf = DirectBufferTLS.getThreadLocal().getReadBuffer();

        try {

            for (int i=0; i < _msg.getNumberReturned(); i++) {
                _objects.offer(DBQueryReplyMessage.readDocument(_myDB._connection.getReadChannel(), buf, true));
            }
        } catch (IOException e) {
            throw new MongoDBException("ERROR : socket error ", e);
        }
    }

    /**
     *  Returns the next object in the cursor.
     *
     * @return next object, or null if there are no more remaining
     * @throws MongoDBException
     */
    public Doc getNextObject() throws MongoDBException {

        if (!isMoreObjects()) {
            refillViaGetMore();
        }

        Doc m = _objects.poll();

        if (m != null) {
            _objectsReturned++;
        }
        
        return m;
    }

    /**
     *  Determins if  objects remain to be consumed by client.  This takes into account
     *  any limit set by the client.  This method can trigger a OP_GET_MORE back to the
     *  database for more objects.
     *
     * @return true if more objects logically remain
     */
    protected boolean isMoreObjects() {

        if (_hardLimit > 0 && _objectsReturned >= _hardLimit) {
            return false;
        }

        if (_objects.size() == 0) {
            try {
                refillViaGetMore();
            }
            catch (MongoDBException e) {
                e.printStackTrace();
            }
        }

        return _objects.size() > 0;
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

        _myDB.sendReadToDB(new DBGetMoreMessage(_myDB.getName(), _collection, _msg.getCursorID() ));

        readAll();        
    }

    /**
     *  Closes the cursor, releasing any server-side cursors and
     *  dumping objects and buffers
     */
    public void close() throws MongoDBException {

        if (_msg.getCursorID() != 0) {
            _myDB.sendReadToDB(new DBKillCursorsMessage(_msg.getCursorID()));
        }

        _objects.clear();

        _closed = true;
    }

    /* --- Enumeration interface --- */

    public boolean hasMoreElements() {

        if (!isMoreObjects()) {
            try {
                refillViaGetMore();
            }
            catch (MongoDBException e) {
                e.printStackTrace();
            }
        }

        return isMoreObjects();
    }

    public Object nextElement() {
        try {
            return getNextObject();
        } catch (MongoDBException e) {
            return null;
        }
    }

    /* --- Iterable interface --- */

    public Iterator<Doc> iterator() {

        return new Iterator<Doc>() {
            public boolean hasNext() {
                return hasMoreElements();
            }

            public Doc next() {
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

    public String toString() {
        return "DBResponse : flags=[" + _msg.getFlags() + "]  cursorID=["
                    + _msg.getCursorID() + "] start=[" + _msg.getStartingFrom() + "] nreturned=[" + _msg.getNumberReturned() + "]";
    }
}
