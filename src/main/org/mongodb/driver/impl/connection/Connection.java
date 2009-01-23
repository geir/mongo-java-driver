package org.mongodb.driver.impl.connection;

import java.nio.channels.SocketChannel;
import java.io.IOException;

public interface Connection {

    public void connect() throws IOException;
    public boolean isConnected();
    public SocketChannel getSocketChannel() throws IOException;
}
