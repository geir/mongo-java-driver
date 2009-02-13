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

import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBQuery;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.Doc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *  Test the limit feature of queries
 */
public class LimitTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_LimitTest");
        _db.getCollection("test").clear();
        assert(_db.getCollection("test").getCount() == 0);
    }

    @Test
    public void testLimit() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");
        testColl.clear();

        Doc[] objs = new Doc[100];

        for (int i = 0; i < 100; i++) {
            objs[i] = new Doc("a", i);
        }

        testColl.insert(objs);

        assert(testColl.getCount() == 100);

        assert(cursorCount(testColl.find()) == 100);

        DBCursor cur = testColl.find(new DBQuery(new MongoSelector(), null, 0, 10));

        int count = cursorCount(cur);
        assert(count == 10);

        cur = testColl.find(new DBQuery(new MongoSelector(), null, 0, 10));
        Doc m = (Doc) cur.nextElement();
        assert(m.getInt("a") == 0);

        cur = testColl.find(new DBQuery(new MongoSelector(), null, 10, 10));
        m = (Doc) cur.nextElement();
        assert(m.getInt("a") == 10);

    }
}
