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
import org.mongodb.driver.ts.IndexInfo;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;

import java.util.List;

public class IndexInfoTest {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_ts_IndexInfoTest");
        _db.getCollection("test").clear();
        assert(_db.getCollection("test").getCount() == 0);
    }

    @Test
    public void testBasic() throws Exception {

        IndexInfo ii = new IndexInfo("woog", "a", "b");
        
        assert(ii.getFields().size() == 2);
        assert(ii.getIndexName().equals("woog"));
        assert(ii.getCollectionName() == null);

        ii.addField("c");
        assert(ii.getFields().size() == 3);
    }

    @Test
    public void testNone() throws Exception {

        DBCollection c = _db.getCollection("test");

        List<IndexInfo> ii = c.getIndexInformation();

        assert(ii.size() == 0);
    }
}
