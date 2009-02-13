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

import java.util.Map;
import java.util.HashMap;
import static org.testng.AssertJUnit.*;
import org.mongodb.driver.ts.Doc;

public class MongoDocTest {

    @Test
    public void orderTest() throws MongoDBException {

        Doc d = new Doc();

        d.put("deleteIndexes", "foo");
        d.put("index", "bar");

        assert(d.size() == 2);
        assert(d.getList().size() == 2);
        assert(d.getList().get(0)._key.equals("deleteIndexes"));
        assert(d.getList().get(1)._key.equals("index"));
    }

    @Test
    public void clearTest() throws MongoDBException {
        Doc d= new Doc();

        assert(d.size() == 0);

        d.put("a", 1);

        assert(d.size() == 1);
        assert(d.getList().size() == 1);

        d.clear();

        assert(d.size() == 0);
        assert(d.getList().size() == 0);
    }

    @Test
    public void mapTest() throws MongoDBException {

        Map<String, Object> m = new HashMap<String, Object>();

        m.put("a", 1);
        m.put("b", 2);

        Doc d = new Doc(m);

        assert(d.size() == 2);
    }

    @Test
    public void verbotenKeyTest() throws MongoDBException {

        Doc m = new Doc();

        try {
            m.put(null, "hi");
            fail();
        }
        catch(Exception e) {
            //
        }

//        try {
//            m.put("a.b", "hi");
//            fail();
//        }
//        catch(Exception e) {
//            //
//        }
//
//        try {
//            m.put("$foo", "hi");
//            fail();
//        }
//        catch(Exception e) {
//            //
//        }
    }
}
