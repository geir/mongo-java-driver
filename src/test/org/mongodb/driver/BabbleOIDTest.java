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

package org.mongodb.driver;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import org.mongodb.driver.ts.BabbleOID;
import org.mongodb.driver.dyn.Mongo;
import org.mongodb.driver.dyn.DB;

import java.util.Iterator;
import java.util.Map;

public class BabbleOIDTest {

    @Test
    public void testTimeFlip() throws Exception {

        Thread.sleep(2000);

        //  call the CTOR twice to move the index value to 1
        BabbleOID oid1 = new BabbleOID();
        oid1 = new BabbleOID();

        Thread.sleep(1100);

        BabbleOID oid2 = new BabbleOID();

        byte[] arr1 = oid1.getArray();
        byte[] arr2 = oid2.getArray();

        assertTrue("time not right", arr2[0] - arr1[0] == 1);  // low order byte for seconds
        assertTrue("index not right : arr[9]" + arr1[9], arr1[9] == 1);  // reset index
        assertTrue("index not reset", arr2[9] == 0);  // reset index
    }

    @Test
    public void testVsBabble() throws Exception {

        DB db = new Mongo().getDB("babbleid");

        Iterator<Map> it = db.getCollection("foo").find();

        while(it.hasNext()) {
            Map m = it.next();

            System.out.println(m);
        }
    }

    @Test
    public void testFormat() throws Exception  {

        byte[] data = new byte[12];

        for (int i=0; i < 12; i++) {
            data[i] = (byte) i;
        }

        BabbleOID boid = new BabbleOID(data);

        assert(boid.toString().equals("000102030405060708090A0B"));
    }

    @Test
    public void testForma2t() throws Exception  {

        BabbleOID boid = new BabbleOID("000102030405060708090A0B");

        assert(boid.toString().equals("000102030405060708090A0B"));
    }

    @Test
    public void generationTime() throws Exception {

        int count = 1000000;

        BabbleOID[] arr = new BabbleOID[count];
        long start = System.currentTimeMillis();

        for (int i=0; i<count; i++) {
            arr[i] = new BabbleOID();
        }

        long end = System.currentTimeMillis();

        System.out.println("BabbleOID gen rate : " + (1.0 * count / (end - start) * 1000) + " objects / second");
    }

}
