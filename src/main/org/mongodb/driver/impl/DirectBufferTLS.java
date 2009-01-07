package org.mongodb.driver.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 */
public class DirectBufferTLS {

    ByteBuffer _readBuf;
    ByteBuffer _writeBuf;

    static ThreadLocal<DirectBufferTLS> _tl = new ThreadLocal<DirectBufferTLS>();

    public DirectBufferTLS() {
        _readBuf = ByteBuffer.allocateDirect(1024*100);
        _readBuf.order(ByteOrder.LITTLE_ENDIAN);
        _writeBuf = ByteBuffer.allocateDirect(1024*100);
        _writeBuf.order(ByteOrder.LITTLE_ENDIAN);
        _tl.set(this);
    }

    public ByteBuffer getReadBuffer() {
        return _readBuf;
    }

    public ByteBuffer getWriteBuffer() {
        return _writeBuf;
    }

    public static DirectBufferTLS getThreadLocal() {
        return _tl.get();
    }

    public void unset() {
        _tl.remove();
    }
}
