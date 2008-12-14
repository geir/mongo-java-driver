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

import org.mongodb.driver.ts.DBQuery;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.MongoDBException;

import java.nio.ByteBuffer;

/**
 *   Query message for MongoDB.  Message format is :
 *
 *   int  : query opcode
 *   cstr : collection name
 *   int  : number to skip
 *   int  : number to return
 *   bson : query object
 *   bson : fields to return (optional)
 *
 */
public class DBQueryMessage extends DBMessage {

    protected final DBQuery _query;
    protected final String _dbName;
    protected final String _collection;

    protected DBQueryMessage(ByteBuffer buf) throws MongoDBException {
        super(buf);

        readInt(); // reserved for future use - mongo might call this "options" in the comments.  or it may not.

        String s = readString();

        // we get in format if <dbname>.<collectionname> so split
        
        String[] ss = s.split("\\.");

        assert(ss.length == 2);
        _dbName = ss[0];
        _collection = ss[1];

        _query = new DBQuery();
        _query.setNumberToSkip(readInt());
        _query.setNumberToReturn(readInt());
        _query.setCompleteQuery(readMongoDoc());

        if (_buf.position() < _buf.limit()) {

            MongoDoc m = readMongoDoc();

            if (m != null) {
                _query.setReturnFieldsSelector(new MongoSelector(m.getMap()));
            }
        }
    }
    
    public DBQueryMessage(String dbName, String collection, DBQuery q) throws MongoDBException {
        super(MessageType.OP_QUERY);
        _query = q;
        _dbName = dbName;
        _collection = collection;
        
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
        writeInt(_query.getNumberToSkip());
        writeInt(_query.getNumberToReturn());
        writeMongoDoc(_query.getCompleteQuery());

        if(_query.getReturnFieldsSelector() != null) {
            writeMongoDoc(_query.getReturnFieldsSelector());
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[OP_QUERY(");
        sb.append(_dbName);
        sb.append(".");
        sb.append(_collection);
        sb.append("):");
        sb.append(_query);
        sb.append("]");

        return sb.toString();
    }
}
