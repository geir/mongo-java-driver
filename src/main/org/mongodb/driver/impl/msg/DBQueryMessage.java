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
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.util.DBStaticData;
import org.mongodb.driver.impl.msg.DBMessage;

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

    public DBQueryMessage(String dbName, String collection, DBQuery q) throws MongoDBException {
        super(DBStaticData.OP_QUERY);
        _query = q;
        _dbName = dbName;
        _collection = collection;
        
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
        writeInt(_query.getNumberToSkip());
        writeInt(_query.getNumberToReturn());
        writeMongoDoc(_query.getCompleteQuery());

        if(_query.getReturnFieldsSelector() != null) {
            writeMongoDoc(_query.getReturnFieldsSelector());
        }
    }
}
