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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.ts.IndexInfo;


public class ValidationTest extends TestBase {

    DB _db;

    @AfterClass
    public void shutDown() throws Exception {
        _db.close();
    }

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_ValidationTest");
    }

    @Test
    public void testValidation() throws MongoDBException {

        _db.dropCollection("test");
        DBCollection testColl = _db.getCollection("test");
        assert(testColl.createIndex(new IndexInfo("$a_1", "a")));
        
        MongoDoc doc = new MongoDoc();

        doc.put("a", 1);
        testColl.insert(doc);

        DBAdmin admin = _db.getAdmin();

        assert(admin.validateCollection("test"));
    }
}
