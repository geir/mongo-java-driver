/**
*      Copyright (C) 2008 Geir Magnusson Jr
*  
*    Licensed under the Apache License, Version 2.0 (the "License");
*    you may not use this file except in compliance with the License.
*    You may obtain a copy of the License at
*  
*       http://www.apache.org/licenses/LICENSE-2.0
*  
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS,
*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*    See the License for the specific language governing permissions and
*    limitations under the License.
*/

package org.mongodb.driver.impl;

import org.mongodb.driver.DB;
import org.mongodb.driver.DBCollection;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.MongoDoc;
import org.mongodb.driver.DBCursor;
import org.mongodb.driver.DBObjectID;
import org.mongodb.driver.DBQuery;
import org.mongodb.driver.MongoSelector;
import org.mongodb.driver.MongoModifier;
import org.mongodb.driver.IndexInfo;
import org.mongodb.driver.options.DBOptions;
import org.mongodb.driver.options.DBCollectionOptions;
import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.impl.msg.DBInsertMessage;
import org.mongodb.driver.impl.msg.DBMessage;
import org.mongodb.driver.impl.msg.DBMsgMessage;
import org.mongodb.driver.impl.msg.DBQueryMessage;
import org.mongodb.driver.impl.msg.DBRemoveMessage;
import org.mongodb.driver.impl.msg.DBUpdateMessage;

import java.util.List;
import java.util.ArrayList;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.IOException;

/**
 *  Implementation of the DB class
 */
class DBImpl implements DB {
   
    public static final String SYSTEM_NAMESPACE_COLLECTION = "system.namespaces";
    public static final String SYSTEM_INDEX_COLLECTION = "system.indexes";
    public static final String SYSTEM_COMMAND_COLLECTION = "$cmd";

    protected boolean _strictCollections = DEFAULT_COLLECTION_MODE;
    public static final boolean DEFAULT_COLLECTION_MODE = false;

    protected String _dbName;

    protected InetSocketAddress _addr;

    protected Socket _sock;
    private final Object _dbMonitor = new Object();

    protected final Mongo _myMongoServer;

    public DBImpl(Mongo mongo, String dbName) throws MongoDBException {
        this(mongo, dbName, new InetSocketAddress("127.0.0.1", Mongo.DEFAULT_MONGO_PORT));
    }
    
    public DBImpl(Mongo mongo, String dbName, InetSocketAddress addr) throws MongoDBException {
        checkDBName(dbName);

        _myMongoServer = mongo;
        _addr = addr;
        _dbName = dbName;

        try {
            _sock = new Socket(_addr.getAddress(), _addr.getPort());
        }
        catch (IOException e) {
            throw new MongoDBException("Error connecting.", e);
        }
    }

    /**
     *    Returns a list of all collections for this database, including indices
     */
    public List<String> getCollectionNames() throws MongoDBException {

        try {
            DBCursor resp = getCollectionsInfo();

            List<String> list = new ArrayList<String>();
            MongoDoc doc;

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

        MongoDoc md = dbCommand(sel);

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

        MongoDoc md = dbCommand(sel);

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
        _sock.close();
    }

    /**
     *  Sends a message to the database
     * 
     * @param m message to send
     * @throws MongoDBException if a problem
     */
    public void sendMessage(String m) throws MongoDBException {
        DBMsgMessage msg = new DBMsgMessage(m);
        sendToDB(msg);
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
    

    protected DBCursor queryDB(String collection, DBQuery q) throws MongoDBException {

        synchronized(_dbMonitor) {
            sendToDB(new DBQueryMessage(_dbName, collection, q));

            return new DBCursorImpl(this, collection);
        }
    }

    protected boolean removeFromDB(String collection, MongoSelector selector) throws MongoDBException {

        synchronized(_dbMonitor) {
            sendToDB(new DBRemoveMessage(_dbName, collection, selector));
            return true;
        }        
    }

    protected boolean replaceInDB(String collection, MongoSelector selector, MongoDoc obj) throws MongoDBException {
        synchronized(_dbMonitor) {
            sendToDB(new DBUpdateMessage(_dbName, collection, selector, obj, false));
            return true;
        }
    }

    protected MongoDoc repsertInDB(String collection, MongoSelector selector, MongoDoc obj) throws MongoDBException {

        // TODO - if  PKInjector, inject
        
        synchronized(_dbMonitor) {
            sendToDB(new DBUpdateMessage(_dbName, collection, selector, obj, true));
            return obj;
        }
    }

    protected boolean modifyInDB(String collection, MongoSelector selector, MongoModifier obj) throws MongoDBException {
        synchronized(_dbMonitor) {
            sendToDB(new DBUpdateMessage(_dbName, collection, selector, obj, false));
            return true;
        }
    }

    protected int getCount(String collection, MongoSelector query) throws MongoDBException {
        MongoSelector sel = new MongoSelector();

        sel.put("count", collection);
        sel.put("query", query);

        MongoDoc doc = dbCommand(sel);

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

    protected MongoDoc dbCommand(MongoSelector selector) throws MongoDBException {

        DBQuery q = new DBQuery(selector);
        q.setNumberToReturn(1); // tis required to be only 1 return, technally -1?

        synchronized(_dbMonitor) {
            DBCursor cursor = queryDB(SYSTEM_COMMAND_COLLECTION, q);
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

            MongoDoc d;
            while((d = cursor.getNextObject()) != null) {

                String name = (String) d.get("name");

                if (name == null) {
                    throw new MongoDBException("Name of index on return from db was null. Coll = " + this._dbName + "." + collection);
                }

                IndexInfo ii = new IndexInfo(name);

                MongoDoc keys = (MongoDoc) d.get("key");

                if (keys == null) {
                    throw new MongoDBException("Keys for index on return from db was null. Coll = " + this._dbName + "." + collection);
                }

                for(String s : keys.orderedKeyList()) {
                    ii.addField(s);
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

        MongoDoc doc = new MongoDoc();

        doc.put("name", info.getIndexName());
        doc.put("ns", _dbName + "." + collection);

        MongoSelector selector = new MongoSelector();

        for (String s : info.getFields()) {
            selector.put(s, 1);
        }

        doc.put("key", selector);

        synchronized(_dbMonitor) {
            sendToDB(new DBInsertMessage(_dbName, SYSTEM_INDEX_COLLECTION, doc));
            return true;
        }
    }

    protected boolean insertIntoDB(String collection, MongoDoc object) throws MongoDBException {

        //  TODO - get this moved into the database.  This shouldn't be a client requirement.

//        if (object.get("_id") == null) {
//            object.put("_id", new DBObjectID());
//        }

        synchronized(_dbMonitor) {
            sendToDB(new DBInsertMessage(_dbName, collection, object));
            return true;
        }
    }

    protected boolean insertIntoDB(String collection, MongoDoc[] objects) throws MongoDBException {

        //  TODO - get this moved into the database.  This shouldn't be a client requirement.

        for (MongoDoc doc : objects) {

            if (doc.get("_id") == null) {
                doc.put("_id", new DBObjectID());
            }
        }

        synchronized(_dbMonitor) {
            sendToDB(new DBInsertMessage(_dbName, collection, objects));
            return true;
        }
    }

    protected void sendToDB(DBMessage msg) throws MongoDBException {

        synchronized(_dbMonitor) {
            try {
                OutputStream os = _sock.getOutputStream();
                os.write(msg.toByteArray());

//                ByteBuffer buf = msg.getInternalByteBuffer();
//
//                buf.flip();
//
//                os.write(buf.array(), 0, buf.limit());
            } catch (IOException e) {
                throw new MongoDBException("IO Error : ", e);
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
