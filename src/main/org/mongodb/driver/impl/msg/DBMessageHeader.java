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

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

/**
 *  Header for Mongo wire messages
 */
public class DBMessageHeader {

    public final static int HEADER_SIZE = 16;      // size, id, responseto, opcode

    protected int _messageLength = HEADER_SIZE;    // overall message length - header size to start

    protected int _requestID;
    protected int _responseTo;
    protected MessageType _op;

    public DBMessageHeader(){
    }

    public void writeHeader(ByteBuffer buf) {
        buf.putInt(_messageLength);             // holder for the length
        buf.putInt(_requestID);
        buf.putInt(_responseTo);
        buf.putInt(_op.getOpCode());
    }

    public static DBMessageHeader readHeader(SocketChannel sc) throws IOException {

        ByteBuffer headerBuf = ByteBuffer.allocate(HEADER_SIZE);
        headerBuf.order(ByteOrder.LITTLE_ENDIAN);

        headerBuf.position(0);

        long i = 0;

        while(i < HEADER_SIZE) {
            i += sc.read(headerBuf);
        }

        if (i != HEADER_SIZE) {
            throw new IOException("Short read for DB response header. read=" + i);
        }

        headerBuf.flip();
        
        return readHeader(headerBuf);        
    }

    public static DBMessageHeader readHeader(InputStream is ) throws IOException {

        ByteBuffer headerBuf = ByteBuffer.allocate(HEADER_SIZE);
        headerBuf.order(ByteOrder.LITTLE_ENDIAN);

        headerBuf.position(0);

        int i = is.read(headerBuf.array(), 0, HEADER_SIZE);

        if (i != HEADER_SIZE) {
            throw new IOException("Short read for DB response header. read=" + i);
        }

        return readHeader(headerBuf);
    }

    public static DBMessageHeader readHeader(ByteBuffer buf) {

        DBMessageHeader header = new DBMessageHeader();

        header._messageLength = buf.getInt();
        header._requestID = buf.getInt();
        header._responseTo = buf.getInt();

        int opVal = buf.getInt();
        header._op = MessageType.get(opVal);

        return header;
    }

    public MessageType getOperation() {
        return _op;
    }

    public int getMessageLength() {
        return _messageLength;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MessageHeader : op/requestID/responseTo/size : ");
        sb.append(_op);
        sb.append("/");
        sb.append(_requestID);
        sb.append("/");
        sb.append(_responseTo);
        sb.append("/");
        sb.append(_messageLength);
        sb.append("/");

        return sb.toString();
    }
}
