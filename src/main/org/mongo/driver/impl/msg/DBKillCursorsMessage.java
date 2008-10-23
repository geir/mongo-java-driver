package org.mongo.driver.impl.msg;

import org.mongo.driver.MongoDBException;
import org.mongo.driver.util.DBStaticData;


/**
 *   KillCursor message for MongoDB.  Message format is :
 *
 *   int    : number of cursors
 *   long[] : cursors to kill
 *
 */
public class DBKillCursorsMessage extends DBMessage {

    protected final long[] _cursors;
    
    public DBKillCursorsMessage(long[] cursors) throws MongoDBException {
        super(DBStaticData.OP_KILL_CURSORS);
        _cursors = cursors;
        init();
    }

    public DBKillCursorsMessage(long cursor) throws MongoDBException {
        super(DBStaticData.OP_KILL_CURSORS);

        _cursors = new long[1];
        _cursors[0] = cursor;
        
        init();
    }

    /**
     *   Writes the query out to the underlying message byte buffer
     *
     * @throws Exception if something wrong w/ mongoDoc
     */
    protected void init() throws MongoDBException {

        writeInt(0); // reserved for future use - mongo might call this "options" in the comments.  or it may not.
        writeInt(_cursors.length);

        for (int i=0; i < _cursors.length; i++) {
            writeLong(_cursors[i]);
        }
    }
}
