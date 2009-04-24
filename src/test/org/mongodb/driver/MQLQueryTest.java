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
import org.testng.annotations.BeforeClass;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.Doc;

public class MQLQueryTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_MQLQueryTest");
    }

    @Test
    public void testSimpleSelect() throws MongoDBException {

        DBCollection testColl = _db.getCollection("test");
        testColl.clear();

        Doc[] objs = new Doc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new Doc("a", i);
        }

        testColl.insert(objs);

        assert(testColl.getCount() == 10);

     //   assert(cursorCount(_db.executeQuery("select from test")) == 10);
        assert(cursorCount(_db.executeQuery("select from test where a = 2")) == 1);
        assert(cursorCount(_db.executeQuery("select from test where a > 5")) == 4);
        _db.executeQuery("select from test where x > y");
    }
}
