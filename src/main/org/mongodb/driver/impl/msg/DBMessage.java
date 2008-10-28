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

import org.mongodb.driver.MongoDoc;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.util.BSONObject;

import java.util.concurrent.atomic.AtomicInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Base message class for mongo communications
 */
public class DBMessage {

    protected final static int DEFAULT_BUF_SIZE = 1024*100;
    protected final static int HEADER_SIZE = 16;      // size, id, responseto, opcode
    
    private static AtomicInteger _classReqID = new AtomicInteger(1);

    
    protected int _messageLength = HEADER_SIZE;    // overall message length - header size to start
    
    protected int _dataLength;
    protected int _requestID = getNextRequestID();
    protected int _responseTo = 0;
    protected int _op;

    protected ByteBuffer _buf = ByteBuffer.allocate(DEFAULT_BUF_SIZE);

    /**
     *   Creates a DBMessage with correct header written
     * 
     * @param op opcode for db operation
     */
    protected DBMessage(int op) {

        _buf.order(ByteOrder.LITTLE_ENDIAN);
        
        _op = op;

        _buf.position(0);
        _buf.putInt(_messageLength);             // holder for the length
        _buf.putInt(_requestID);
        _buf.putInt(_responseTo);
        _buf.putInt(_op);
        
        assert(_buf.position() == HEADER_SIZE);
    }

    /**
     *  Writes a string to the message buffer.  This is a CSTR, with no leading size.
     *
     * @param s string to write
     */
    protected void writeString(String s) {

        int i = BSONObject.serializeCSTR(_buf, s);
        updateMessageLength(i);
    }

    protected void writeLong(long i) {

        _buf.putLong(i);
        updateMessageLength(8);
    }

    protected void writeInt(int i) {

        _buf.putInt(i);
        updateMessageLength(4);
    }

    protected void writeByte(byte b) {
        _buf.put(b);
        updateMessageLength(1);
    }

    protected void writeMongoDoc(MongoDoc doc) throws MongoDBException {

        BSONObject bson = new BSONObject();

        bson.serialize(doc);

        byte[] arr = bson.toArray();

        _buf.put(arr);

        updateMessageLength(arr.length);
    }
    
    protected void updateMessageLength(int delta) {
        _messageLength += delta;

        _buf.putInt(0, _messageLength);
    }

    protected static int getNextRequestID() {
        return _classReqID.getAndIncrement();
    }


    public ByteBuffer getInternalByteBuffer() {
        return _buf;        
    }

    public byte[] toByteArray() {
        _buf.flip();
        byte[] msg = new byte[_buf.limit()];
        _buf.get(msg);

        return msg;
    }
}