/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mongodb.driver.impl.msg;

import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.MongoDBException;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a dbUpdate mongo operation
 */
public class DBUpdateMessage extends DBMessage {

    protected final String _dbName;
    protected final String _collection;
    protected final MongoSelector _selector;
    protected final MongoDoc _obj;
    protected final boolean _repsert;

    public DBUpdateMessage(String dbName, String collection, MongoSelector sel, MongoDoc obj, boolean repsert)
            throws MongoDBException {
        super(MessageType.OP_UPDATE);
        _dbName = dbName;
        _collection = collection;

        _selector = sel;
        _obj = obj;
        _repsert = repsert;

        init();
    }

    public DBUpdateMessage(ByteBuffer buf) throws MongoDBException {
        super(buf);

        readInt(); // reserved for future use - mongo might call this "options" in the comments.  or it may not.

        String s = readString();
        String[] ss = s.split("\\.");
        assert(ss.length == 2);
        _dbName = ss[0];
        _collection = ss[1];


        _repsert = (readInt() == 1);

        _selector = readMongoSelector();
        _obj = readMongoDoc();
    }

    /**
     *   Writes the query out to the underlying message byte buffer
     *
     * @throws MongoDBException if something wrong w/ mongoDoc
     */
    protected void init() throws MongoDBException {

        writeInt(0); // reserved for future use - mongo might call this "options" in the comments.  or it may not.
        writeString(_dbName + "." + _collection);

        writeInt( _repsert ? 1 : 0);   // 1 if a repsert operation (upsert)
        writeMongoDoc(_selector);
        writeMongoDoc(_obj);
    }
}
