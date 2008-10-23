package org.mongo.driver;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.mongo.driver.impl.Mongo;

/**
 * Tests getting more w/ subsequent fetches
 */
public class QueryGetMoreTest extends TestBase{

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_QueryGetMoreTest");
    }

    @Test
    public void testGetMore() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection c = _db.getCollection("test");

        MongoDoc doc = new MongoDoc("name", "asasdaspoaspdoiaspdoaisdpoasidpaosidaposdiapsodiaposdiaposdias");

        long start = System.currentTimeMillis();

        int num = 100000;
        for (int i=0; i < num; i++) {
            c.insert(doc);
        }

        long end = System.currentTimeMillis();

        System.out.println(1.0 * num / (end-start) * 1000.0 + " inserts per sec");

        start = System.currentTimeMillis();

        DBCursor cursor = c.find();

        int count = 0;
        for (MongoDoc d : cursor) {
            count++;
        }

        end = System.currentTimeMillis();

        System.out.println( 1.0 * num / (end-start) * 1000.0 + " reads per sec");

        assert(count == num);
    }

    public static void main(String[] args) throws Exception {

        QueryGetMoreTest t = new QueryGetMoreTest();

        t.setUp();
        t.testGetMore();
    }
}
