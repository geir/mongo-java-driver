package org.mongodb.driver;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongodb.driver.impl.Mongo;
import org.mongodb.driver.admin.DBAdmin;


public class ValidationTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_ValidationTest");
    }

    @Test
    public void testValidation() throws MongoDBException {

        _db.dropCollection("test");
        DBCollection testColl = _db.getCollection("test");
        assert(testColl.createIndex(new IndexInfo("$a_1", "a")));
        
        MongoDoc doc = new MongoDoc();

        doc.put("a", 1);
        testColl.insert(doc);

        DBAdmin admin = _db.getAdmin();

        assert(admin.validateCollection("test"));
    }
}
