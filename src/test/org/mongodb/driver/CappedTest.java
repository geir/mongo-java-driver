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
import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.MongoDoc;

public class CappedTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_CappedTest");
    }

    @Test
    public void testCap() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection testColl = _db.createCollection("test", new DBCollectionOptions().setCapped(2000, 10));

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
    public void testCapOptionRetrieval1() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection testColl = _db.createCollection("test", new DBCollectionOptions().setCapped(2000, 10));

        for (int i=0; i < 20; i++) {
            testColl.insert(new MongoDoc("name", i));
        }

        assert(testColl.getCount() == 10);

        DBCollectionOptions options = testColl.getOptions();

        assert(options.isCapped());
        assert(options.isReadOnly());
        assert(options.getCappedSizeLimit() == 2000);
        assert(options.getCappedObjectMax() == 10);
    }

    @Test
    public void testCapOptionRetrieval2() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection testColl = _db.createCollection("test", new DBCollectionOptions().setCapped(2000));

        for (int i=0; i < 20; i++) {
            testColl.insert(new MongoDoc("name", i));
        }

        assert(testColl.getCount() == 20);

        DBCollectionOptions options = testColl.getOptions();

        assert(options.isCapped());
        assert(options.isReadOnly());
        assert(options.getCappedSizeLimit() == 2000);
        assert(options.getCappedObjectMax() == DBCollectionOptions.DB_DEFAULT);
    }

    @Test
    public void testNoCapOptionRetrieval() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection testColl = _db.createCollection("test");

        for (int i=0; i < 20; i++) {
            testColl.insert(new MongoDoc("name", i));
        }

        assert(testColl.getCount() == 20);

        DBCollectionOptions options = testColl.getOptions();

        assert(!options.isCapped());
        assert(options.isReadOnly());
        assert(options.getCappedSizeLimit() == DBCollectionOptions.DB_DEFAULT);
        assert(options.getCappedObjectMax() == DBCollectionOptions.DB_DEFAULT);
    }
}
