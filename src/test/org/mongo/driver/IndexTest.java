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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.mongo.driver.impl.Mongo;

import java.util.List;

/**
 *  Tests for working with indexes
 */
public class IndexTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_IndexTest");
        _db.dropCollection("test");
    }

    @Test
    public void testSingle() throws MongoDBException {
        DBCollection testColl = _db.getCollection("test");

        assert(testColl.getIndexInformation().size() == 0);

        assert(testColl.createIndex(new IndexInfo("$a_1", "a")));

        List<IndexInfo> list = testColl.getIndexInformation();

        assert(list.size() == 1);

        IndexInfo ii = list.get(0);

        assert(ii.getIndexName().equals("$a_1"));
        assert(ii.getFields().size() == 1);
        assert(ii.getFields().get(0).equals("a"));
        assert(ii.getCollectionName().equals("test"));
    }

    @Test
    public void testRemovalOnCollectionDrop() throws MongoDBException {

        _db.dropCollection("test");
        DBCollection testColl = _db.getCollection("test");

        assert(testColl.getIndexInformation().size() == 0);

        assert(testColl.createIndex(new IndexInfo("$a_1", "a")));
        List<IndexInfo> list = testColl.getIndexInformation();
        assert(list.size() == 1);

        _db.dropCollection("test");
        assert(testColl.getIndexInformation().size() == 0);        
    }

    @Test
    public void testMulti() throws MongoDBException {

        _db.dropCollection("test");
        DBCollection testColl = _db.getCollection("test");

        assert(testColl.getIndexInformation().size() == 0);

        assert(testColl.createIndex(new IndexInfo("$a_1", "a", "b")));

        List<IndexInfo> list = testColl.getIndexInformation();

        assert(list.size() == 1);

        IndexInfo ii = list.get(0);

        assert(ii.getIndexName().equals("$a_1"));
        assert(ii.getFields().size() == 2);
        assert(ii.getFields().get(0).equals("a"));
        assert(ii.getFields().get(1).equals("b"));
        assert(ii.getCollectionName().equals("test"));

        assert(testColl.createIndex(new IndexInfo("$b_1", "c", "d", "e")));

        list = testColl.getIndexInformation();

        assert(list.size() == 2);

        ii = list.get(0);

        assert(ii.getIndexName().equals("$a_1"));
        assert(ii.getFields().size() == 2);
        assert(ii.getFields().get(0).equals("a"));
        assert(ii.getFields().get(1).equals("b"));
        assert(ii.getCollectionName().equals("test"));

        ii = list.get(1);

        assert(ii.getIndexName().equals("$b_1"));
        assert(ii.getFields().size() == 3);
        assert(ii.getFields().get(0).equals("c"));
        assert(ii.getFields().get(1).equals("d"));
        assert(ii.getFields().get(2).equals("e"));
        assert(ii.getCollectionName().equals("test"));

    }

}
