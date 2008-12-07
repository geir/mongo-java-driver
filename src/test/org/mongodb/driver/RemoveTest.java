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

package org.mongodb.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.ts.MongoSelector;

public class RemoveTest extends TestBase {

    DB _db;
   
    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_RemoveTest");
        _db.getCollection("test").clear();

        assert(_db.getCollection("test").getCount() == 0);
    }

    @Test
    void testClear() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");

        MongoDoc doc = new MongoDoc();

        doc.put("a", 1);
        testColl.insert(doc);
        doc.clear();
        doc.put("a", 2);
        testColl.insert(doc);

        assert(testColl.getCount() == 2);

        testColl.clear();

        assert(testColl.getCount() == 0);
    }

    @Test
    void testRemove() throws MongoDBException {
        _db.dropCollection("remove");
        DBCollection testColl = _db.getCollection("remove");

        assert(testColl.getCount() == 0);

        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);
        assert(testColl.getCount() == 10);

        MongoSelector ms = new MongoSelector("a", 1);

        testColl.remove(ms);
        assert(testColl.getCount() == 9);
        assert(cursorCount(testColl.find(ms)) == 0);

        ms.put("a", 3);

        testColl.remove(ms);
        assert(testColl.getCount() == 8);
        assert(cursorCount(testColl.find(ms)) == 0);

        ms.put("a", 5);

        testColl.remove(ms);
        assert(testColl.getCount() == 7);
        assert(cursorCount(testColl.find(ms)) == 0);
    }
}
