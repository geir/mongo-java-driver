package org.mongo.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongo.driver.impl.Mongo;
import org.mongo.driver.options.collection.CappedCollection;
import org.mongo.driver.options.impl.CollectionOptions;
import org.mongo.driver.options.impl.MongoOption;

import java.util.HashMap;
import java.util.Map;

public class CappedTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_CappedTest");
    }

    @Test
    public void testCap() throws MongoDBException {

        _db.dropCollection("test");

        CappedCollection co = new CappedCollection(2000, 10);

        DBCollection testColl = _db.createCollection("test", co);

        for (int i=0; i < 10; i++) {
            testColl.insert(new MongoDoc("name", i));
        }

        assert(testColl.getCount() == 10);

        for (int i=0; i < 10; i++) {
            testColl.insert(new MongoDoc("name", i));
        }
        assert(testColl.getCount() == 10);        
    }

    @Test
    public void testCapOptionRetrieval() throws MongoDBException {

        _db.dropCollection("test");

        CappedCollection co = new CappedCollection(2000, 10);

        DBCollection testColl = _db.createCollection("test", co);

        for (int i=0; i < 20; i++) {
            testColl.insert(new MongoDoc("name", i));
        }

        assert(testColl.getCount() == 10);

        boolean found = false;

        for (MongoOption o : testColl.getOptions()) {

            if (o instanceof CappedCollection) {
                found = true;
            }
        }

        assert(found);
    }

    @Test
    public void testNoCapOptionRetrieval() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection testColl = _db.createCollection("test");

        for (int i=0; i < 20; i++) {
            testColl.insert(new MongoDoc("name", i));
        }

        assert(testColl.getCount() == 20);

        boolean found = false;

        for (MongoOption o : testColl.getOptions()) {

            if (o instanceof CappedCollection) {
                found = true;
            }
        }

        assert(!found);
    }

}
