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
import org.mongodb.driver.ts.options.DBOptions;
import org.mongodb.driver.ts.DB;

import static org.testng.AssertJUnit.*;

/**
 *  Tests if strict collection mode works.
 *
 *  Strict collection mode - collections can only be gotten if they exist, and created if they dont
 */
public class StrictCollectionTest  extends TestBase {

    DB _db;

    @BeforeClass
    public void setUp() throws Exception{
        _db = new Mongo().getDB("org_mongo_driver_StrictCollectionTest");
    }

    @AfterClass
    public void shutDown() throws Exception {
        _db.close();
    }
    
    @Test
    public void testStrictGet() throws MongoDBException {

        _db.dropCollection("test");

        DBOptions scm = new DBOptions().setStrictCollectionMode(true);
        _db.setDBOptions(scm);

        try {
            _db.getCollection("test");
            fail();
        }
        catch(MongoDBException e) {
            // expected
        }
    }

    @Test
    public void testStrictCreate() throws MongoDBException {

        _db.dropCollection("test");

        _db.setDBOptions(new DBOptions().setStrictCollectionMode(true));

        _db.createCollection("test");

        try {
            _db.createCollection("test");
            fail();
        }
        catch(MongoDBException e) {
            // expected
        }
    }
}
