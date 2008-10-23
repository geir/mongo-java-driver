package org.mongo.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongo.driver.impl.Mongo;

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
