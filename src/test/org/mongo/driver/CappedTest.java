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
