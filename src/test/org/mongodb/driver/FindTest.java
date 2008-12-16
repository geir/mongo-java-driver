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
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FindTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_FindTest");
        _db.getCollection("test").clear();
        assert(_db.getCollection("test").getCount() == 0);
    }

    @Test
    public void testFind() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");
        testColl.clear();

        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);

        assert(testColl.getCount() == 10);

        assert(cursorCount(testColl.find()) == 10);

        MongoSelector sel = new MongoSelector("a", 2);

        assert(cursorCount(testColl.find(sel)) == 1);
        assert(cursorCount(testColl.find(new DBQuery(sel))) == 1);

        sel.put("a", new MongoSelector("$gt", 5));

        assert(cursorCount(testColl.find(sel)) == 4);
        assert(cursorCount(testColl.find(new DBQuery(sel))) == 4);
    }

    @Test
    public void testMapFind() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");

        testColl.clear();

        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);

        assert(testColl.getCount() == 10);
        assert(cursorCount(testColl.find()) == 10);

        Map<String, Object> sel = new HashMap<String, Object>();

        sel.put("a", 2);

        assert(cursorCount(testColl.find(sel)) == 1);
        assert(cursorCount(testColl.find(new DBQuery(sel))) == 1);

        sel.put("a", new MongoSelector("$gt", 5));

        assert(cursorCount(testColl.find(sel)) == 4);
        assert(cursorCount(testColl.find(new DBQuery(sel))) == 4);

        Map<String, Object> sel2 = new HashMap<String, Object>();

        sel2.put("$gt", 5);

        sel.put("a", sel2);

        assert(cursorCount(testColl.find(sel)) == 4);
        assert(cursorCount(testColl.find(new DBQuery(sel))) == 4);
    }

    @Test
    public void testRegexpFind() throws MongoDBException {

        DBCollection testColl = _db.getCollection("test");

        testColl.clear();

        testColl.insert(new MongoDoc("name", "geir"));
        testColl.insert(new MongoDoc("name", "neir"));
        testColl.insert(new MongoDoc("name", "ged"));

        assert(testColl.getCount() == 3);

        assert(cursorCount(testColl.find(new MongoSelector("name", Pattern.compile("eir")))) == 2);
        assert(cursorCount(testColl.find(new MongoSelector("name", Pattern.compile("g")))) == 2);
        assert(cursorCount(testColl.find(new MongoSelector("name", Pattern.compile("n")))) == 1);
        assert(cursorCount(testColl.find(new MongoSelector("name", Pattern.compile("ed")))) == 1);
    }

    @Test
    public void testStringFind() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");
        testColl.clear();

        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);

        assert(testColl.getCount() == 10);

        assert(cursorCount(testColl.find()) == 10);

        assert(cursorCount(testColl.find("this.a == 2")) == 1);
        assert(cursorCount(testColl.find("this.a == 3")) == 1);

        assert(cursorCount(testColl.find("this.a > 5")) == 4);
    }

}
