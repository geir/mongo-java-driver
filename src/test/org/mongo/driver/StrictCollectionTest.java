package org.mongo.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import org.mongo.driver.impl.Mongo;
import org.mongo.driver.options.collection.CappedCollection;
import org.mongo.driver.options.impl.MongoOption;
import org.mongo.driver.options.db.StrictCollectionMode;

import static org.testng.AssertJUnit.*;

/**
 *  Tests if strict collection mode works.
 *
 *  Strict collection mode - collections can only be gotten if they exist, and created if they dont
 */
public class StrictCollectionTest  extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_StrictCollectionTest");
    }

    @Test
    public void testStrictGet() throws MongoDBException {

        _db.dropCollection("test");

        StrictCollectionMode scm = new StrictCollectionMode();
        _db.setDBOptions(scm);

        try {
            _db.getCollection("test");
            fail();
        }
        catch(MongoDBException e) {
            // expected
        }
    }

    @Test
    public void testStrictCreate() throws MongoDBException {

        _db.dropCollection("test");

        StrictCollectionMode scm = new StrictCollectionMode();
        _db.setDBOptions(scm);

        DBCollection testColl = _db.createCollection("test");

        try {
            _db.createCollection("test");
            fail();
        }
        catch(MongoDBException e) {
            // expected
        }
    }
}
