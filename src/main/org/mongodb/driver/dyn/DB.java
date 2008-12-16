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

package org.mongodb.driver.dyn;

import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.ts.options.DBOptions;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.MongoDBIOException;

import java.util.List;
import java.util.Map;

/**
 *  A Mongo database instance.
 *
 */
public interface DB {

    /**
     *  Returns a list of the names of the collections  in this database
     *
     * @return List of collection names
     * @throws org.mongodb.driver.MongoDBException on error
     * @throws MongoDBIOException if communications error
     */
    public List<String> getCollectionNames() throws MongoDBException, MongoDBIOException;

    /**
     *  Creates a new collection collection.  Like getCollection() relies upon the
     *  strict mode for creation
     *
     * @param name name of collection to create
     * @return collection
     * @throws MongoDBException if collection doesn't exist and in strict mode, or if
     *         there's a problem creating the collection
     * @throws MongoDBIOException if communications error
     */
    public Collection createCollection(String name) throws MongoDBException, MongoDBIOException;

    /**
     *  Creates a collection with optional options.  Note that if options are passed in and not in strict
     *  mode, driver doesn't currently guarantee the options will be respected.
     *
     *  You can :
     *
     * 1) Set the collection as "capped", putting 'sizeLimit' and  optionally 'maxObjects' in the map :
     *
     *     sizeLimit - int size in bytes
     *     maxObjects - int number of objects max to keep in the collection
     *
     * 2) Set the collection initial extent, putting 'initialExtent' into the map
     *
     *     initialExtent - int size in bytes
     * 
     * @param name name of collection to create
     * @param options optinoal options for creation (e.g. CappedCollection)
     * @return collection
     * @throws MongoDBException if collection exists and in strict mode or an error creating collection
     * @throws MongoDBIOException if communications error
     */
    public Collection createCollection(String name, Map options) throws MongoDBException, MongoDBIOException;

    /**
     *  Gets a DBCollection object representing the specified collection in the database.
     *  This collection object can be used for subsequent operations.  Note that this
     *  will create the collection if it doesn't exist.  If you wish to be strict,
     *  use getCollection(name, option) to enforce the requirement that the collection
     *  must exist.
     *
     * @param name the name of collection to get
     * @return collection object for subsequent operations, or null if it doesn't exist.
     * @throws MongoDBException on error
     * @throws MongoDBIOException if communications error
     */
    public Collection getCollection(String name) throws MongoDBException, MongoDBIOException;


    /**
     *  Drops a collection, the collections object, and the colletions indexes
     *
     * @param name Name of collection to drop
     * @return true if successful, false otherwise
     * @throws MongoDBException if error
     * @throws MongoDBIOException if communications error
     */
    public boolean dropCollection(String name) throws MongoDBException, MongoDBIOException;

    /**
     *  Returns the name of this database
     *
     * @return name of database
     */
    public String getName();

    /**
     *  Return the admin interface for this DB
     * @return admin object for this database
     */
    public DBAdmin getAdmin();

    /**
     * Returns the options for this database.  E.g. require collections exist
     * @return current options settings for this database
     */
    public DBOptions getDBOptions();

    /**
     * Sets the options for this database
     *
     * @param dbOptions options for database
     */
    public void setDBOptions(DBOptions dbOptions);

    public void resetDBOptions();

    public void close() throws Exception;

   // DB.prototype.eval (and dbEval, which is same)
}
