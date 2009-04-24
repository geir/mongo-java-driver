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

package org.mongodb.driver.impl;

import org.testng.annotations.Test;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.commands.ListDatabasesCmd;
import org.mongodb.driver.ts.options.DBOptions;
import static org.testng.AssertJUnit.*;

public class DBTest {

    @Test
    public void testDBName() throws Exception {

        Mongo m = new Mongo();

        try {
            new DBImpl(null,null, null);
            fail();
        }
        catch(Exception e) {
            // ok
        }

        try {
            new DBImpl(m, null,"");
            fail();
        }
        catch(Exception e) {
            // ok
        }

        try {
            new DBImpl(m, null,"a.b");
            fail();
        }
        catch(Exception e) {
            // ok
        }

        DBImpl db = new DBImpl(m, m.getConnection(), "test_me");
        
        assert(db.getName().equals("test_me"));

        db.close();
    }

    @Test
    public void testCreateDrop() throws Exception {

        Mongo m = new Mongo();

        DB db = m.getDB("org_mongo_driver_DBTest");

        for (String n : db.getCollectionNames()) {
            db.dropCollection(n);
        }

        assert(db.getCollectionNames().size() == 0);
        db.getCollection("a");
        assert(db.getCollectionNames().size() == 1);
        db.getCollection("b");
        assert(db.getCollectionNames().size() == 2);
        db.dropCollection("a");
        assert(db.getCollectionNames().size() == 1);
        db.dropCollection("b");
        assert(db.getCollectionNames().size() == 0);

        db.close();
    }

    @Test
    public void testStrictCollectionCreation() throws Exception {

        Mongo m = new Mongo();

        DB db = new DBImpl(m, m.getConnection(), "org_mongo_driver_DBTest");
        db.setDBOptions(new DBOptions().setStrictCollectionMode(true));

        for (String n : db.getCollectionNames()) {
            db.dropCollection(n);
        }

        assert(db.getCollectionNames().size() == 0);

        try {
            db.getCollection("woogie");
            fail();
        }
        catch(Exception e) {
            // expect an exception as we're in strict mode
        }
        
        assert(db.getCollectionNames().size() == 0);

        db.setDBOptions(null);

        assert(db.getCollection("woogie") != null);
        assert(db.getCollectionNames().size() == 1);

    }

    @Test
    public void testGetDatabaseNames() throws Exception {

        Mongo m = new Mongo();

        DB db = m.getDB("admin");

        ListDatabasesCmd cmd = new ListDatabasesCmd();

        db.executeCommand(cmd);        
    }

}
