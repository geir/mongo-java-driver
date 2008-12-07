package org.mongodb.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.admin.ProfileInfo;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DBCursor;

import java.util.List;

public class ProfilingTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_ProfilingTest");
    }

    @Test
    public void testProfileLevel() throws MongoDBException {

        DBAdmin admin = _db.getAdmin();

        admin.setProfilingLevel(DBAdmin.ProfileLevel.OFF);
        assert(admin.getProfilingLevel() == DBAdmin.ProfileLevel.OFF);

        admin.setProfilingLevel(DBAdmin.ProfileLevel.SLOW_ONLY);
        assert(admin.getProfilingLevel() == DBAdmin.ProfileLevel.SLOW_ONLY);

        admin.setProfilingLevel(DBAdmin.ProfileLevel.ALL);
        assert(admin.getProfilingLevel() == DBAdmin.ProfileLevel.ALL);

        admin.setProfilingLevel(DBAdmin.ProfileLevel.OFF);
    }


    @Test
    public void testProfiling() throws MongoDBException {

        DBAdmin admin = _db.getAdmin();

        admin.setProfilingLevel(DBAdmin.ProfileLevel.ALL);

        assert(admin.getProfilingLevel() == DBAdmin.ProfileLevel.ALL);

        DBCollection c = _db.getCollection("foo");

        DBCursor cur = c.find();

        cur.close();

        List<ProfileInfo> l = admin.getProfilingInfo();

        assert(l.size() > 0);
    }

}
