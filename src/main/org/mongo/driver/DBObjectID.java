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

package org.mongo.driver;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *   UUID that's quasi-compatible with the 10gen JS appserver ObjectID
 *
 *   TODO - this needs to be fixed.  It would be nice to scrunch it down
 *   to 12 bytes so we don't have to re-wtite it, but it needs to be fixed
 *   either way
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
