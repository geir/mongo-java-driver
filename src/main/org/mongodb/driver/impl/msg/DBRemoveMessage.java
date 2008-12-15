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

import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.MongoDBException;

import java.nio.ByteBuffer;

/**
 * Representas a Mongo Delete operation
 */
public class DBRemoveMessage extends DBMessage {

    protected final String _dbName;
    protected final String _collection;
    protected final MongoSelector _selector;


    protected DBRemoveMessage(ByteBuffer buf) throws MongoDBException {
        super(buf);

        readInt(); // reserved for future use - mongo might call this "options" in the comments.  or it may not.

        String s = readString();

        // we get in format if <dbname>.<collectionname> so split

        String[] ss = s.split("\\.");

        assert(ss.length == 2);
        _dbName = ss[0];
        _collection = ss[1];

        readInt();  // flags?

        _selector = new MongoSelector(readMongoDoc().getMap());
    }

    public DBRemoveMessage(String dbName, String collection, MongoSelector sel) throws MongoDBException {
        super(MessageType.OP_DELETE);
        _dbName = dbName;
        _collection = collection;

        if (sel == null) {
           throw new MongoDBException("Error : can't have a null selector for DBRemove");
        }
        _selector = sel;

        init();
    }

    /**
     *   Writes the query out to the underlying message byte buffer
     *
     * @throws MongoDBException if something wrong w/ mongoDoc
     */
    protected void init() throws MongoDBException {

        writeInt(0); // reserved for future use - mongo might call this "options" in the comments.  or it may not.
        writeString(_dbName + "." + _collection);

        writeInt(0);   // flags ?
        writeMongoDoc(_selector);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[DELETE(");
        sb.append(_dbName);
        sb.append(".");
        sb.append(_collection);
        sb.append("):");
        sb.append(headerString());
        sb.append(":");
        sb.append(_selector);
        sb.append("]");

        return sb.toString();
    }

}
