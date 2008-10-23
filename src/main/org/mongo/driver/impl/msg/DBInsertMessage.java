package org.mongo.driver.impl.msg;

import org.mongo.driver.MongoDoc;
import org.mongo.driver.MongoDBException;
import org.mongo.driver.util.DBStaticData;
import org.mongo.driver.impl.msg.DBMessage;

/**
string collection;
      a series of JSObjects terminated with a null object (i.e., just EOO)
 */
public class DBInsertMessage extends DBMessage {

    protected final String _dbName;
    protected final String _collection;
    protected final MongoDoc[] _objs;

    public DBInsertMessage(String dbName, String collection, MongoDoc obj) throws MongoDBException {
        super(DBStaticData.OP_INSERT);
        _dbName = dbName;
        _collection = collection;

        MongoDoc[] arr = new MongoDoc[1];
        arr[0] = obj;

        _objs = arr;
        
        init();
    }

    public DBInsertMessage(String dbName, String collection, MongoDoc[] objs) throws MongoDBException {
        super(DBStaticData.OP_INSERT);
        _dbName = dbName;
        _collection = collection;
        _objs = objs;

        init();
    }

    /**
     *   Writes the query out to the underlying message byte buffer
     *
     * @throws Exception if something wrong w/ mongoDoc
     */
    protected void init() throws MongoDBException {

        writeInt(0); // reserved for future use - mongo might call this "options" in the comments.  or it may not.
        writeString(_dbName + "." + _collection);

        for (MongoDoc doc : _objs) {
            writeMongoDoc(doc);
        }
    }
}
