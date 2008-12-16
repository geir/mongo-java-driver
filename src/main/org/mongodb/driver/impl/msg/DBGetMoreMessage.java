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

import org.mongodb.driver.MongoDBException;
import java.nio.ByteBuffer;

/**
 *  Message class representing a "get more" message, a message used by a cursor
 *  to fetch another batch of data from a query
 */
public class DBGetMoreMessage extends DBMessage {

    protected final long _cursor;
    protected final String _dbName;
    protected final String _collection;
    protected final int _numberToReturn;


    public DBGetMoreMessage(ByteBuffer buf) throws MongoDBException {
        super(buf);

        readInt(); // reserved for future use - mongo might call this "options" in the comments.  or it may not.

        String s = readString();
        String[] ss = s.split("\\.");
        assert(ss.length == 2);
        _dbName = ss[0];
        _collection = ss[1];

        _numberToReturn = readInt();

        _cursor = readLong();
    }

    public DBGetMoreMessage(String dbName, String collection, long cursor) throws MongoDBException
    {
        super(MessageType.OP_GET_MORE);
        _dbName = dbName;
        _collection = collection;
        _cursor = cursor;
        _numberToReturn = 0;  // for now, set to 0 to leave it up to the DB
        init();
    }

    /**
     *   Writes the query out to the underlying message byte buffer
     *
     * @throws MongoDBException if something wrong w/ mongoDoc
     */
    protected void init() throws MongoDBException
    {
        writeInt(0); // reserved for future use - mongo might call this "options" in the comments.  or it may not.
        writeString(_dbName + "." + _collection);
        writeInt(_numberToReturn); // n toreturn - leave it up to db for now
        writeLong(_cursor);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[GETMORE(");
        sb.append(_dbName);
        sb.append(".");
        sb.append(_collection);
        sb.append("):");
        sb.append(headerString());
        sb.append(":");
        sb.append("nReturn[").append(_numberToReturn).append("]");
        sb.append(" cursor[").append(_cursor).append("]");
        sb.append("]");

        return sb.toString();
    }

}
