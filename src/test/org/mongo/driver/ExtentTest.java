package org.mongo.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongo.driver.impl.Mongo;
import org.mongo.driver.options.collection.CappedCollection;
import org.mongo.driver.options.collection.InitialExtent;
import org.mongo.driver.options.impl.CollectionOptions;
import org.mongo.driver.options.impl.MongoOption;

import java.util.HashMap;
import java.util.Map;

public class ExtentTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_ExtentTest");
    }

    @Test
    public void testExtent() throws MongoDBException {

        _db.dropCollection("test");

        InitialExtent co = new InitialExtent(2000);

        DBCollection testColl = _db.createCollection("test", co);

        boolean found = false;
        for (MongoOption o : testColl.getOptions()) {

            if (o instanceof InitialExtent) {
                found = true;
                assert(((InitialExtent) o).getSizeInBytes() == 2000);
            }
        }

        assert(found);
    }
}
