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

import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.MongoDBException;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

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
 *  Query reply header :
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

    public static final int REPLY_HEADER_SIZE = 20;

    protected int _flags;
    protected long _cursorID;
    protected int _startingFrom;
    protected int _numberReturned;
    protected List<MongoDoc> _objects = new ArrayList<MongoDoc>();
    
    protected DBQueryReplyMessage(ByteBuffer buf) throws MongoDBException {
        this(buf, true);
    }

    public  DBQueryReplyMessage(ByteBuffer buf, boolean nibbleObjects) throws MongoDBException {
        super(buf);

        _flags = readInt();
        _cursorID = readLong();
        _startingFrom = readInt();
        _numberReturned = readInt();

        if (nibbleObjects) {
            for (int i=0; i < _numberReturned; i++) {
                _objects.add(readMongoDoc());
            }
        }
    }

    /**
     * Reads the next BSON doc off of the wire using the provided bytebuffer.
     * 
     * @param sc  channel to read from
     * @param buf buffer to use
     * @param validate if you want the keys checked.  Currently ignored
     * @return a MongoDoc
     * @throws MongoDBException in case things go wrong
     */
    public static MongoDoc readDocument(SocketChannel sc, ByteBuffer buf, boolean validate) throws MongoDBException {

        return DBMessage.readMongoSelector(sc, buf);
    }


    /**
     *  Reads the full headers from  the wire into the buffer.
     *
     * @param buf  buffer to write into
     * @param sc  channel to read from
     * @return number of bytes read
     * @throws IOException on error
     */
    public static long fillBufferWithHeaders(ByteBuffer buf, SocketChannel sc) throws IOException {

        int readSize = DBMessageHeader.HEADER_SIZE + REPLY_HEADER_SIZE;

        buf.clear();
        buf.limit(readSize);

        long bytesRead = 0;
        while (bytesRead < readSize) {
            bytesRead += sc.read(buf);
        }

        assert(bytesRead == readSize);

        buf.flip();

        return bytesRead;
    }

    public int getFlags() {
        return _flags;
    }

    public int getNumberReturned() {
        return _numberReturned;
    }

    public long getCursorID() {
        return _cursorID;
    }

    public int getStartingFrom() {
        return _startingFrom;
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
