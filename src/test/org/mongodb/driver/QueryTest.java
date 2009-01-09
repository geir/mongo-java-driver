/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mongodb.driver;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import static org.testng.AssertJUnit.fail;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBQuery;

import java.util.HashMap;

/**
 * Tests for some basics re queries
 */
public class QueryTest extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_QueryTest");
    }

    @AfterClass
    public void shutDown() throws Exception {
        _db.close();
    }

    @Test
    public void testErrorOnNoIndex() throws MongoDBException {

        _db.dropCollection("test");

        DBCollection c = _db.getCollection("test");

        MongoDoc doc = new MongoDoc("name", "asasdaspoaspdoiaspdoaisdpoasidpaosidaposdiapsodiaposdiaposdias");

        int num = 100000;
        for (int i=0; i < num; i++) {
            doc.put("i", i);
            c.insert(doc);
        }


        DBQuery dbq = new DBQuery(new HashMap());

        dbq.setOrderBy(new MongoSelector("i", 1));

        try {
            c.find(dbq);
            fail();
        }
        catch(MongoDBQueryException mdbe){
            // ignore
        }

    }
}
