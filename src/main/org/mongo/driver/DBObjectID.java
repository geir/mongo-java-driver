package org.mongo.driver;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *   UUID that's quasi-compatible with the 10gen JS appserver ObjectID
 */
public class DBObjectID {

    protected static final int SIZE = 12;

    byte[] arr = new byte[SIZE];

    public DBObjectID() {

        UUID ug = UUID.randomUUID();

        ByteBuffer buf  = ByteBuffer.allocate(16);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // is this the right order?

        buf.putLong(ug.getMostSignificantBits());
        buf.putLong(ug.getLeastSignificantBits());

        buf.flip();

        buf.get(arr); 
    }

    public DBObjectID(byte[] data) {
        System.arraycopy(data, 0, arr, 0, SIZE);
    }

    public byte[] getArray() {
        return arr;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (int i=0; i < SIZE; i++) {
            sb.append(arr[i]);
        }

        return sb.toString();
    }
}
