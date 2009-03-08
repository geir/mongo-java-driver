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

import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.MongoDBIOException;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBQuery;
import org.mongodb.driver.ts.IndexInfo;
import org.mongodb.driver.ts.Doc;
import org.mongodb.driver.ts.options.DBOptions;
import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.impl.msg.DBInsertMessage;
import org.mongodb.driver.impl.msg.DBMessage;
import org.mongodb.driver.impl.msg.DBMsgMessage;
import org.mongodb.driver.impl.msg.DBQueryMessage;
import org.mongodb.driver.impl.msg.DBRemoveMessage;
import org.mongodb.driver.impl.msg.DBUpdateMessage;
import org.mongodb.driver.impl.connection.Connection;
import org.mongodb.mql.MQL;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *  Implementation of the DB class
 */
public class DBImpl implements DB {

    public static final String SYSTEM_NAMESPACE_COLLECTION = "system.namespaces";
    public static final String SYSTEM_INDEX_COLLECTION = "system.indexes";
    public static final String SYSTEM_COMMAND_COLLECTION = "$cmd";

    protected boolean _strictCollections = DEFAULT_COLLECTION_MODE;
    public static final boolean DEFAULT_COLLECTION_MODE = false;

    protected String _dbName;

    protected Connection _connection;

//    protected SocketChannel _socketChannel;

    protected final Object _dbMonitor = new Object();

    protected final MongoImpl _myMongoServer;

    public DBImpl(MongoImpl mongo, Connection c, String dbName) throws MongoDBException {
        checkDBName(dbName);

        _myMongoServer = mongo;
        _dbName = dbName;

        try {

            _connection = c;
            _connection.connect();
            
            /*
             *  set the TLS for our direct ByteBuffers.  Gets us out of needing to pool, since
             *  each DBImplis *NOT* threadsafe, so this should work just fine.
             */
            DirectBufferTLS tls = new DirectBufferTLS();
            tls.set();
        }
        catch (IOException e) {
            throw new MongoDBException("Error connecting.", e);
        }
    }

    public DBCursor executeQuery(String query) throws MongoDBException, MongoDBIOException {

        if (query == null) {
            throw new NullPointerException("MQL query is null");
        }

        MQL mql = new MQL(query);   // TODO - add query cache?

        DriverQueryInfo dqi = new DriverQueryInfo();
        mql.populateQueryInfo(dqi);

        return dqi.execQuery(this);
    }

    public Doc executeCommand(org.mongodb.driver.ts.commands.DBCommand command) throws MongoDBException {
        return null;
    }


    /**
     *    Returns a list of all collections for this database, including indices
     */
    public List<String> getCollectionNames() throws MongoDBException {

        try {
            DBCursor resp = getCollectionsInfo();

            List<String> list = new ArrayList<String>();
            Doc doc;

            while( (doc = resp.getNextObject()) != null) {

                String s = (String) doc.get("name");

                if (s != null && s.length() > 0) {
                        // strip off db name
                    if (s.startsWith(_dbName)) {
                        s = s.substring(_dbName.length() + 1);
                    }

                    list.add(s);
                }
            }

            return list;
        }
        catch (Exception e) {
            throw new MongoDBException(e);
        }
    }

    protected DBCursor getCollectionsInfo() throws MongoDBException {
        return getCollectionsInfo(null);
    }

    protected DBCursor getCollectionsInfo(String collName) throws MongoDBException {

        MongoSelector sel = (collName == null?  new MongoSelector() : new MongoSelector("name", _dbName + "." + collName));  // FIX having _db + here

        try {
            DBQuery q = new DBQuery(sel);
            return queryDB(SYSTEM_NAMESPACE_COLLECTION, q);
        }
        catch (Exception e) {
            throw new MongoDBException(e);
        }
    }

    /**
     *  Creates a collection if not in strict mode.  if in strict mode, check for existence.
     *  If exist, throw an exception
     *
     * @param name name of collection to create
     */
    public DBCollection createCollection(String name) throws MongoDBException {

        return createCollection(name, null);
    }

    /**
     *  Creates a collection with optional options.  Note that if options are passed in and not in strict
     *  mode, driver doesn't currently guarantee the options will be respected.
     *
     * @param name name of collection to create
     * @param options optinoal options for creation (e.g. CappedCollection)
     * @return collection
     * @throws MongoDBException if collection exists and in strict mode or an error creating collection
     */
    public DBCollection createCollection(String name, DBCollectionOptions options) throws MongoDBException {

        /*
         * first, check existence
         */

        List<String> list = getCollectionNames();

        for(String s : list) {
            if (name.equals(s)) {
                if (isStrictCollections()) {
                    throw new MongoDBException("Collection " + name + " exists.  Currently in strict mode");
                }
                else {
                    return new DBCollectionImpl(this, s);
                }
            }
        }

        /*
         * if not, create and return it
         */

        /*
         *  Note : The Mongo wire protocol requires that the "create" entry is first in the eventual BSON as it's the
         *  db command to create...
         *
         *  TODO - ask dwight o fix this
         */
        MongoSelector sel = new MongoSelector("create", name);

        if (options != null) {
            sel.add(options.getSelector());
        }

        Doc md = dbCommand(sel);

        Object o = md.get("ok");

        if (o != null && o instanceof Number) {
            if (((Number)o).intValue() == 1) {
                return new DBCollectionImpl(this, name);
            }
        }

        throw new MongoDBException("Error creating collection : " + md);
    }

    /**
     *  Returns the name of this database
     *
     * @return name of database
     */
    public String getName() {
        return _dbName;
    }

    public DBAdmin getAdmin() {
        return new DBAdminImpl(this);
    }

    public DBCollection getCollection(String name) throws MongoDBException {

        /*
         * first, see if it exists
         */
        List<String> list = getCollectionNames();

        for(String s : list) {
            if (name.equals(s)) {
                return new DBCollectionImpl(this, s);
            }
        }

        /*
         * if not, either create a new one or throw if strict
         */
        if (isStrictCollections()) {
            throw new MongoDBException("Collection " + name + " doesn't exist.  Currently in strict mode");
        }

        return createCollection(name);
    }

    public boolean dropCollection(String name) throws MongoDBException {

        /*
         *  Mongo currently requires us to drop the indexes for the collection manually
         */

        DBCollection coll = null;

        try {
            coll = getCollection(name);
        }
        catch(MongoDBException e) {
            // assume in strict mode and it doesn't exist?
        }

        if (coll == null) {
            return true; // nothing to drop, I guess
        }

        coll.dropIndexes();

        /*
         * now drop the collection itself
         */
        MongoSelector sel = new MongoSelector("drop", name);

        Doc md = dbCommand(sel);

        Object o = md.get("ok");

        if (o != null && o instanceof Number) {
            if (((Number)o).intValue() == 1) {
                return true;
            }
        }

        return false;
    }


    /**
     * Closes the connection to the database.  After this method is called,
     * this DB object is useless.
     *
     * @throws Exception if a problem
     */
    public void close() throws Exception {
        _connection.close();

        DirectBufferTLS tls = DirectBufferTLS.getThreadLocal();

        if (tls != null) {
            tls.unset();
        }
    }

    /**
     *  Sends a message to the database
     *
     * @param m message to send
     * @throws MongoDBException if a general problem
     */
    public void sendMessage(String m) throws MongoDBException {
        DBMsgMessage msg = new DBMsgMessage(m);
        sendWriteToDB(msg);
    }


    /**
     * Returns the options for this database.  E.g. require collections exist
     * @return current options for this db
     */
    public DBOptions getDBOptions() {
        return null;
    }

    /**
     * Sets the options for this database
     *
     * @param dbOptions options affecting all collections and behaviors of this db
     */
    public void setDBOptions(DBOptions dbOptions) {

        resetDBOptions();

        if (dbOptions == null) {
            return;
        }

        _strictCollections = dbOptions.isStrictCollectionMode();
    }

    public void resetDBOptions() {
        _strictCollections = DEFAULT_COLLECTION_MODE;
    }

    public boolean isStrictCollections() {
        return _strictCollections;
    }

    public Doc eval(String function, Object... args) throws MongoDBException {

        MongoSelector cmd = new MongoSelector("$eval", function);
        cmd.put("args", args);

        return dbCommand(cmd);
    }


    protected DBCursor queryDB(String collection, DBQuery q) throws MongoDBException {

        synchronized(_dbMonitor) {
            sendReadToDB(new DBQueryMessage(_dbName, collection, q));

            return new DBCursorImpl(this, collection, q.getNumberToReturn());
        }
    }

    protected boolean removeFromDB(String collection, MongoSelector selector) throws MongoDBException {

        synchronized(_dbMonitor) {
            sendWriteToDB(new DBRemoveMessage(_dbName, collection, selector));
            return true;
        }
    }

    protected boolean replaceInDB(String collection, MongoSelector selector, Doc obj) throws MongoDBException {
        synchronized(_dbMonitor) {
            sendWriteToDB(new DBUpdateMessage(_dbName, collection, selector, obj, false));
            return true;
        }
    }

    protected Doc repsertInDB(String collection, MongoSelector selector, Doc obj) throws MongoDBException {

        // TODO - if  PKInjector, inject

        synchronized(_dbMonitor) {
            sendWriteToDB(new DBUpdateMessage(_dbName, collection, selector, obj, true));
            return obj;
        }
    }

    protected boolean modifyInDB(String collection, MongoSelector selector, Doc obj) throws MongoDBException {
        synchronized(_dbMonitor) {
            sendWriteToDB(new DBUpdateMessage(_dbName, collection, selector, obj, false));
            return true;
        }
    }

    protected int getCount(String collection, MongoSelector query) throws MongoDBException {
        MongoSelector sel = new MongoSelector();

        sel.put("count", collection);
        sel.put("query", query);

        Doc doc = dbCommand(sel);

        // first check return code

        Object o = doc.get("ok");

        if (o != null && o instanceof Number) {
            if (((Number)o).intValue() == 1) {

                o = doc.get("n");

                if (o != null && o instanceof Number) {
                    return ((Number)o).intValue();
                }
            }
        }

        throw new MongoDBException("Error with count command " + doc.toString());
    }

    protected boolean dropIndex(String collection, String name) throws MongoDBException {

        MongoSelector sel = new MongoSelector();

        sel.put("deleteIndexes", collection);
        sel.put("index", name);
       
        return dbCommand(sel) != null;
    }

    protected Doc dbCommand(MongoSelector command) throws MongoDBException {
        
        synchronized(_dbMonitor) {
            DBCursor cursor = queryDB(SYSTEM_COMMAND_COLLECTION, new DBCommand(command));
            return cursor.getNextObject();
        }
    }

    protected List<IndexInfo> getIndexInformation(String collection) throws MongoDBException {

        // get the indexes only for the specified collection

        MongoSelector sel = new MongoSelector("ns", _dbName + "." + collection);

        DBQuery q = new DBQuery(sel);

        synchronized(_dbMonitor) {
            DBCursor cursor = queryDB(SYSTEM_INDEX_COLLECTION, q);

            List<IndexInfo> list = new ArrayList<IndexInfo>();

            Doc d;
            while((d = cursor.getNextObject()) != null) {

                String name = (String) d.get("name");

                if (name == null) {
                    throw new MongoDBException("Name of index on return from db was null. Coll = " + this._dbName + "." + collection);
                }

                IndexInfo ii = new IndexInfo(name);

                Doc keys = (Doc) d.get("key");

                if (keys == null) {
                    throw new MongoDBException("Keys for index on return from db was null. Coll = " + this._dbName + "." + collection);
                }

                for (Map.Entry<String, Object> e : keys.entrySet()) {
                    ii.addField(e.getKey());
                }

                String ns = (String) d.get("ns");

                if (ns == null) {
                    throw new MongoDBException("Namespace for index on return from db was null. Coll = " + this._dbName + "." + collection);
                }

                ns = ns.substring(ns.indexOf(".") + 1);
                assert(ns.equals(collection));

                ii.setCollectionName(ns);
                list.add(ii);
            }

            return list;
        }
    }

    protected boolean createIndex(String collection, IndexInfo info) throws MongoDBException {

        Doc doc = new Doc();

        doc.put("name", info.getIndexName());
        doc.put("ns", _dbName + "." + collection);

        MongoSelector selector = new MongoSelector();

        for (String s : info.getFields()) {
            selector.put(s, 1);
        }

        doc.put("key", selector);

        synchronized(_dbMonitor) {
            sendWriteToDB(new DBInsertMessage(_dbName, SYSTEM_INDEX_COLLECTION, doc));
            return true;
        }
    }

    protected boolean insertIntoDB(String collection, Doc object) throws MongoDBException {

        synchronized(_dbMonitor) {
            sendWriteToDB(new DBInsertMessage(_dbName, collection, object));
            return true;
        }
    }

    protected boolean insertIntoDB(String collection, Doc[] objects) throws MongoDBException {

        synchronized(_dbMonitor) {
            sendWriteToDB(new DBInsertMessage(_dbName, collection, objects));
            return true;
        }
    }

    protected void sendWriteToDB(DBMessage msg) throws MongoDBIOException {

        synchronized(_dbMonitor) {
            try {
                ByteBuffer buf = msg.getInternalByteBuffer();

                buf.flip();

                _connection.getWriteChannel().write(buf);
//                _socketChannel.write(buf);
                
            } catch (IOException e) {
                throw new MongoDBIOException("IO Error : ", e);
            }
        }
    }

    protected void sendReadToDB(DBMessage msg) throws MongoDBIOException {

        synchronized(_dbMonitor) {
            try {
                ByteBuffer buf = msg.getInternalByteBuffer();

                buf.flip();

                _connection.getReadChannel().write(buf);
//                _socketChannel.write(buf);

            } catch (IOException e) {
                throw new MongoDBIOException("IO Error : ", e);
            }
        }
    }

    private void checkDBName(String s) throws MongoDBException {

        if (s != null) {
            if (s.length() > 0) {
                if (s.indexOf(".") == -1) {
                    return;
                }
            }
        }

        throw new MongoDBException("Invalid DB Name");
    }
}
