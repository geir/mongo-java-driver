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

package org.mongodb.driver.dyn.impl;

import org.mongodb.driver.dyn.DB;
import org.mongodb.driver.dyn.Collection;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.impl.DBImpl;
import org.mongodb.driver.impl.MongoImpl;
import org.mongodb.driver.ts.options.DBOptions;
import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.admin.DBAdmin;

import java.util.List;
import java.util.Map;

/**
 *  The "dynamic language" facade for a DBImpl
 */
public class DynDBImpl implements DB {

    final protected DBImpl _db;

    public DynDBImpl(DBImpl db) {
        _db = db;
    }

    public DynDBImpl(MongoImpl mongo, String dbName) throws MongoDBException {
        _db = new DBImpl(mongo, mongo.getConnection(), dbName);
    }

    public List<String> getCollectionNames() throws MongoDBException {

        return _db.getCollectionNames();
    }

    public Collection createCollection(String name) throws MongoDBException {

        return new CollectionImpl(_db.createCollection(name));
    }

    public Collection createCollection(String name, Map options) throws MongoDBException {

        if (options == null || options.size() == 0) {
            return new CollectionImpl(_db.createCollection(name));
        }

        DBCollectionOptions dco = new DBCollectionOptions();

        Object o = options.get("sizeLimit");
        Object p = options.get("maxObjects");
        Object q = options.get("initialExtent");

        if (o != null) {

            if (p == null) {
                dco.setCapped((Integer) o);
            }
            else {
                dco.setCapped((Integer) o, (Integer) p);
            }
        }
        else {
            if (q != null) {
                dco.setInitialExtent((Integer)q);
            }
        }
        
        return new CollectionImpl(_db.createCollection(name, dco));
    }

    public Collection getCollection(String name) throws MongoDBException {

        return new CollectionImpl(_db.getCollection(name));
    }

    public boolean dropCollection(String name) throws MongoDBException {
        return _db.dropCollection(name);
    }

    public String getName() {
        return _db.getName();
    }

    public DBAdmin getAdmin() {
        return _db.getAdmin();
    }

    public DBOptions getDBOptions() {
        return _db.getDBOptions();
    }

    public void setDBOptions(DBOptions dbOptions) {
        _db.setDBOptions(dbOptions);
    }

    public void resetDBOptions() {
        _db.resetDBOptions();
    }

    public void close() throws Exception {
        _db.close();
    }
}
