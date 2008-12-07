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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.MongoDBException;

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
