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

public class ExtentTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_ExtentTest");
    }

    @Test
    public void testExtent() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection testColl = _db.createCollection("test", new DBCollectionOptions().setInitialExtent(2000));

        DBCollectionOptions options = testColl.getOptions();

        assert(!options.isCapped());
        assert(options.isReadOnly());
        assert(options.getInitialExtent() == 2000);
        assert(options.getCappedObjectMax() == DBCollectionOptions.DB_DEFAULT);
    }
}
