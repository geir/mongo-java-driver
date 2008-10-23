package org.mongo.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongo.driver.impl.Mongo;

import java.util.Map;
import java.util.HashMap;

public class InsertTest {

    DB _db;
    
    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_InsertTest");
        _db.getCollection("test").clear();
        assert(_db.getCollection("test").getCount() == 0);
    }

    @Test
    void testInsert() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");

        MongoDoc doc = new MongoDoc();

        doc.put("a", 1);
        testColl.insert(doc);
        assert(testColl.getCount() == 1);

        doc.clear();
        doc.put("a", 2);
        testColl.insert(doc);

        assert(testColl.getCount() == 2);

        MongoSelector ms = new MongoSelector();

        ms.put("a", 2);
        assert(testColl.getCount(ms) == 1);
    }

    @Test
    void testInsertMulti() throws MongoDBException {
        DBCollection testColl = _db.getCollection("multi");

        testColl.clear();

        assert(testColl.getCount() == 0);


        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);
        assert(testColl.getCount() == 10);

        MongoSelector ms = new MongoSelector("a", 2);

        assert(testColl.getCount(ms) == 1);
    }

    @Test
    void testInsertClear() throws MongoDBException {
        DBCollection testColl = _db.getCollection("clear");

        testColl.clear();

        assert(testColl.getCount() == 0);

        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);
        assert(testColl.getCount() == 10);

        testColl.clear();
        
        assert(testColl.getCount() == 0);
    }

    @Test
    void testCount() throws MongoDBException {
        _db.dropCollection("clear");

        DBCollection testColl = _db.getCollection("clear");

        assert(testColl.getCount() == 0);

        MongoDoc[] objs = new MongoDoc[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);
        assert(testColl.getCount() == 10);

        for (int i = 0; i < 10; i++) {
            objs[i] = new MongoDoc("a", i);
        }

        testColl.insert(objs);
        assert(testColl.getCount() == 20);

        MongoSelector ms = new MongoSelector("a", 2);

        assert(testColl.getCount(ms) == 2);
    }

    @Test
    void testMapInsert() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");

        testColl.clear();

        Map doc = new HashMap();

        doc.put("a", 1);
        testColl.insert(doc);
        assert(testColl.getCount() == 1);

        doc.clear();
        doc.put("a", 2);
        testColl.insert(doc);

        assert(testColl.getCount() == 2);

        MongoSelector ms = new MongoSelector();

        ms.put("a", 2);
        assert(testColl.getCount(ms) == 1);
    }

}
