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
