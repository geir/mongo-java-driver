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
