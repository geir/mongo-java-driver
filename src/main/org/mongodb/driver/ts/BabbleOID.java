/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mongodb.driver.ts;

import org.mongodb.driver.MongoDBException;

import java.util.Random;
import java.util.Formatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 *  Implementation of the Babble OID.
 * 
 * 12 bytes
 * ---
 *  0 time
 *  1
 *  2
 *  3
 *  4 machine
 *  5
 *  6
 *  7 pid
 *  8
 *  9 inc
 * 10
 * 11
 */
public class BabbleOID {

    protected static final int SIZE = 12;
    protected static final byte[] MACHINE = new byte[3]; // can't get ethernet address in java 5
    protected static final byte[] PID = new byte[2];     // can't portably get pid in java 5

    protected final static AtomicInteger _index = new AtomicInteger();
    protected final static Object _indexLock = new Object();
    protected static int _indexTime = getObjectIDTime();

    /*
     *  Babble has had a bug, so we're going to simply support the byte-string mapping that it does for
     *  backwards compatibility
     */
    protected final static int[] _flap = {7,6,5,4,3,2,1,0, 11, 10, 9, 8};

    static {
        Random r = new Random(System.currentTimeMillis());
        r.nextBytes(MACHINE);
        r.nextBytes(PID);
    }
    
    byte[] _arr = new byte[SIZE];

    public BabbleOID() {

        ByteBuffer buf  = ByteBuffer.allocate(12);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int time = getObjectIDTime();
        
        buf.putInt(time);

        buf.put(MACHINE);
        buf.put(PID);

        int index = getIndex(time);

        buf.put((byte) (index & 0xFF));
        buf.put((byte) ((index >> 8) & 0xFF));
        buf.put((byte) ((index >> 16) & 0xFF));

        buf.flip();

        buf.get(_arr);
    }

    public BabbleOID(String id) throws MongoDBException{

        if (id.length() != 24) {
            throw new MongoDBException("Invalid length - must be 24 characters in string");
        }

        for (int i = 0; i < id.length() / 2; i++) {
            int x = Integer.parseInt(id.substring(i*2, i*2 + 2), 16);
            _arr[_flap[i]] = (byte) x;
        }
    }

    public BabbleOID(byte[] data) {
        System.arraycopy(data, 0, _arr, 0, SIZE);
    }

    /**
     *  returns an "object ID time" - which is the current 4 byte seconds
     *
     * @return current time in seconds
     */
    public static int getObjectIDTime() {
        return (int) (System.currentTimeMillis() / 1000); // time in seconds
    }

    /**
     *  Returns the current index value for the given second.
     *
     * @param time current time to be used in the object ID
     * @return index
     */
    private int getIndex(int time) {
        synchronized(_indexLock) {
            if (time != _indexTime) {
                _index.set(0);
                _indexTime = time;
            }
            return _index.getAndIncrement();
        }
    }

    /**
     * returns the OID in byte form
     * @return array of bytes for this OID
     */
    public byte[] getArray() {

        byte[] copy = new byte[SIZE];
        System.arraycopy(_arr, 0, copy, 0, SIZE);

        return copy;
    }

    /**
     *  Hex string representation of the OID
     * @return string
     */
    public String toString() {

        StringBuilder to_return = new StringBuilder();
        Formatter formatter = new Formatter(to_return);

        for (int i=0; i < _arr.length; i++) {
            byte aByte = _arr[_flap[i]];
            formatter.format("%02X", aByte);
        }
        return to_return.toString();
    }
}
