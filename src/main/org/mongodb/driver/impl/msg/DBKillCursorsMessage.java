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

package org.mongodb.driver.impl.msg;

import org.mongodb.driver.MongoDBException;

import java.io.InputStream;
import java.io.IOException;


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
        super(MessageType.OP_KILL_CURSORS);

        _cursors = new long[cursors.length];
        System.arraycopy(cursors, 0, _cursors, 0, cursors.length);
        init();
    }

    public DBKillCursorsMessage(long cursor) throws MongoDBException {
        super(MessageType.OP_KILL_CURSORS);

        _cursors = new long[1];
        _cursors[0] = cursor;
        
        init();
    }

    /**
     *   Writes the query out to the underlying message byte buffer
     *
     * @throws MongoDBException if something wrong w/ mongoDoc
     */
    protected void init() throws MongoDBException {

        writeInt(0); // reserved for future use - mongo might call this "options" in the comments.  or it may not.
        writeInt(_cursors.length);

        for (long _cursor : _cursors) {
            writeLong(_cursor);
        }
    }
    
    /**
     *  Read this kind of message object out of an input stream
     *
     * @param is stream to read from
     */
    public void read(InputStream is ) throws IOException {
    }    
}
