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

package org.mongodb.driver.dyn;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongodb.driver.MongoDBException;


import java.util.Map;
import java.util.Iterator;

public class DynIteratorTest extends TestBase{

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_dyn_IteratorTest");
        _db.getCollection("test").clear();
        assert(_db.getCollection("test").getCount() == 0);
    }

    @Test
    public void testDynFind() throws MongoDBException {
        Collection testColl = _db.getCollection("test");
        testColl.clear();

        Map<String, Object>[] objs = new Map[10];

        for (int i = 0; i < 10; i++) {
            objs[i] = newMap("a", i);
        }

        testColl.insert(objs);

        assert(testColl.getCount() == 10);

        Iterator<Map> ii = testColl.find();

        int i = 0;
        while(ii.hasNext()) {
            if (ii.hasNext()) {
                assert(ii.next() != null);
                i++;
            }
        }

        assert(i == 10);
    }
}
