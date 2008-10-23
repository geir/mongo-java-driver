package org.mongo.driver.impl.msg;

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
