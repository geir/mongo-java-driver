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
 *   Query response message from MongoDB.  Message format is :
 *
 *  Normal message Header :
 *
 *    int  : total message length
 *    int  : requestID
 *    int  : responseTo
 *    int  : opcode
 *
 *  Query reponse header :
 *
 *    int  : flags
 *    long : cursorID
 *    int  : starting from...
 *    int  : number returned
 *    bson : the bson objects returned
 *
 *  This class currently isn't used by the driver - it's here for completeness
 *  for reading response messages from server for other purposes.
 *
 */
public class DBQueryReplyMessage extends DBMessage {

    protected int _flags;
    protected long _cursorID;
    protected int _startingFrom;
    protected int _numberReturned;
    protected List<MongoDoc> _objects = new ArrayList<MongoDoc>();

    protected DBQueryReplyMessage(ByteBuffer buf) throws MongoDBException {
        super(buf);

        _flags = readInt();
        _cursorID = readLong();
        _startingFrom = readInt();
        _numberReturned = readInt();

        for (int i=0; i < _numberReturned; i++) {
            _objects.add(readMongoDoc());
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[REPLY:").append(headerString()).append(":").append(" flags[");
        sb.append(_flags);
        sb.append("] cursorID=[");
        sb.append(_cursorID);
        sb.append("] start[");
        sb.append(_startingFrom);
        sb.append("] #returned[");
        sb.append(_numberReturned);
        sb.append("]]");

        return sb.toString();
    }
}
