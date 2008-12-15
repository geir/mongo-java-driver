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

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

/**
string collection;
      a series of JSObjects terminated with a null object (i.e., just EOO)
 */
public class DBInsertMessage extends DBMessage {

    protected final String _dbName;
    protected final String _collection;
    protected final MongoDoc[] _objs;

    public DBInsertMessage(ByteBuffer buf) throws MongoDBException {
        super(buf);

        readInt(); // reserved for future use - mongo might call this "options" in the comments.  or it may not.

        String s = readString();
        String[] ss = s.split("\\.");
        assert(ss.length == 2);
        _dbName = ss[0];
        _collection = ss[1];

        List<MongoDoc> objs = new ArrayList<MongoDoc>();

        while(buf.position() < buf.limit()) {
            objs.add(readMongoDoc());
        }

        _objs = objs.toArray(new MongoDoc[objs.size()]);        
    }
    
    public DBInsertMessage(String dbName, String collection, MongoDoc obj) throws MongoDBException {
        super(MessageType.OP_INSERT);
        _dbName = dbName;
        _collection = collection;

        MongoDoc[] arr = new MongoDoc[1];
        arr[0] = obj;

        _objs = arr;
        
        init();
    }

    public DBInsertMessage(String dbName, String collection, MongoDoc[] objs) throws MongoDBException {
        super(MessageType.OP_INSERT);
        _dbName = dbName;
        _collection =   collection;

        _objs = new MongoDoc[objs.length];
        System.arraycopy(objs, 0, _objs, 0, objs.length);

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

        for (MongoDoc doc : _objs) {
            writeMongoDoc(doc);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[INSERT(");
        sb.append(_dbName);
        sb.append(".");
        sb.append(_collection);
        sb.append("):");

        sb.append(" number[");
        sb.append(_objs.length);
        sb.append("]");
        
        return sb.toString();
    }

}
