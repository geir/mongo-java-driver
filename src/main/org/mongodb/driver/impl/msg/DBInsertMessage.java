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

import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.util.DBStaticData;
import org.mongodb.driver.impl.msg.DBMessage;

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
