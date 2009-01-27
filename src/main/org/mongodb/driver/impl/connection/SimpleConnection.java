package org.mongodb.driver.impl.connection;

import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.io.IOException;

/**
 *   Simple connection - connects to a single mongo server
 */
public class SimpleConnection implements Connection {

    private SocketChannel _socketChannel = null;
    private InetSocketAddress _inetAddr;

    public SimpleConnection(InetSocketAddress addr) {

        _inetAddr = addr;
    }

    public void connect() throws IOException {

        if (_socketChannel != null) {
            if (_socketChannel.isConnected()) {
                return;
            }
        }

        _socketChannel = SocketChannel.open(_inetAddr);
    }

    
    public boolean isConnected() {
        return _socketChannel != null && _socketChannel.isConnected();
    }

    public SocketChannel getReadChannel() throws IOException {
        return getChannel();
    }

    public SocketChannel getWriteChannel() throws IOException {
        return getChannel();
    }

    private SocketChannel getChannel() throws IOException {

        if (!isConnected()) {
            connect();
        }

        return _socketChannel;
    }

    public void close() {

        if (!isConnected()) {
            return;
        }

        try {
            _socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
