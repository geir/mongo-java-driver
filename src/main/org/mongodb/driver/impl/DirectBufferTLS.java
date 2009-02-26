package org.mongodb.driver.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;

/**
 *  Little class to carry thread-specific buffers and similar - things
 *  that are too heavy to allocate over and over.
 */
public class DirectBufferTLS {

    private ByteBuffer _readBuf;
    private ByteBuffer _writeBuf;
    private CharBuffer _charBuffer = null;
    private final CharsetEncoder _encoder = Charset.forName("UTF-8").newEncoder();

    private static ThreadLocal<DirectBufferTLS> _tl = new ThreadLocal<DirectBufferTLS>();

    public DirectBufferTLS() {
        _readBuf = ByteBuffer.allocateDirect(1024*150);
        _readBuf.order(ByteOrder.LITTLE_ENDIAN);
        _writeBuf = ByteBuffer.allocateDirect(1024*150);
        _writeBuf.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void set() {
        _tl.set(this);
    }

    public CharsetEncoder getEncoder() {
        _encoder.reset();
        return _encoder;
    }

    public CharBuffer getCharBuffer(int sizeNeeded) {
        if (_charBuffer == null || _charBuffer.capacity() < sizeNeeded) {
            _charBuffer = CharBuffer.allocate(sizeNeeded * 5);
        }

        _charBuffer.clear();
        return _charBuffer;
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
