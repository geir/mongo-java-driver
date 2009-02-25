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

    ByteBuffer _readBuf;
    ByteBuffer _writeBuf;
    CharBuffer _charBuffer = null;
    final CharsetEncoder _encoder = Charset.forName("UTF-8").newEncoder();

    static ThreadLocal<DirectBufferTLS> _tl = new ThreadLocal<DirectBufferTLS>();

    /**
     * CTOR that does allocation and attaches as a threadlocal.
     */
    public DirectBufferTLS() {
        this(true);
    }

    /**
     *  CTOR that does allocation, but has option to not be a TLS for local use
     *
     * @param setInTLS true if the instance should be set as the thread local
     */
    public DirectBufferTLS(boolean setInTLS) {
        _readBuf = ByteBuffer.allocateDirect(1024*50);
        _readBuf.order(ByteOrder.LITTLE_ENDIAN);
        _writeBuf = ByteBuffer.allocateDirect(1024*50);
        _writeBuf.order(ByteOrder.LITTLE_ENDIAN);

        if (setInTLS) {
            _tl.set(this);
        }            
    }

    public CharsetEncoder getEncoder() {
        _encoder.reset();
        return _encoder;
    }

    public CharBuffer getCharBuffer(int sizeNeeded) {
        if (_charBuffer == null || _charBuffer.capacity() < sizeNeeded) {
            _charBuffer = CharBuffer.allocate(sizeNeeded * 2);
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
