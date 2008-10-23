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

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * User: geir
 * Date: Oct 14, 2008
 * Time: 8:43:45 AM
 */
public class DBMessageHeader {

    protected final static int HEADER_SIZE = 16;      // size, id, responseto, opcode

    protected int _messageLength = HEADER_SIZE;    // overall message length - header size to start

    protected int _size;
    protected int _requestID;
    protected int _responseTo;
    protected int _op;

    ByteBuffer _headerBuf = ByteBuffer.allocate(HEADER_SIZE);

    public DBMessageHeader(){
        _headerBuf.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void readHeader(InputStream is ) throws IOException {

        _headerBuf.position(0);

        int i = is.read(_headerBuf.array(), 0, HEADER_SIZE);

        if (i != HEADER_SIZE) {
            throw new IOException("Short read for DB response header");
        }

        _size = _headerBuf.getInt();
        _requestID = _headerBuf.getInt();
        _responseTo = _headerBuf.getInt();
        _op = _headerBuf.getInt();
    }
}
