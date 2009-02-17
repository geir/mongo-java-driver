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

package org.mongodb.driver.ts;

import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.ts.options.DBOptions;
import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.ts.commands.DBCommand;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.MongoDBIOException;

import java.util.List;

/**
 *  A Mongo database instance.
 * 
 */
public interface DB {

    /**
     *  Executes a query on this database.
     *
     *  @param query MQL query string
     *  @return cursor with result set
     *  @throws MongoDBException if something goes awry
     *  @throws MongoDBIOException on IO error with database
     */
    public DBCursor executeQuery(String query) throws MongoDBException, MongoDBIOException;


    /**
     *  Executes a command on the database
     *
     *  @param command to execute
     *  @return MongoDoc with the results
     *  @throws MongoDBException if something goes awry
     *  @throws MongoDBIOException on IO error with database
     */
    public Doc executeCommand(DBCommand command) throws MongoDBException;


    /**
     *  Returns a list of the names of the collections  in this database
     *
     * @return List of collection names
     * @throws MongoDBException on error
     * @throws MongoDBIOException on IO error with database
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
     * @throws MongoDBIOException on IO error with database
     */
    public DBCollection createCollection(String name) throws MongoDBException, MongoDBIOException;

    /**
     *  Creates a collection with optional options.  Note that if options are passed in and not in strict
     *  mode, driver doesn't currently guarantee the options will be respected.
     *
     * @param name name of collection to create
     * @param options optinoal options for creation (e.g. CappedCollection)
     * @return collection
     * @throws MongoDBException if collection exists and in strict mode or an error creating collection
     * @throws MongoDBIOException on IO error with database
     */
    public DBCollection createCollection(String name, DBCollectionOptions options) throws MongoDBException, MongoDBIOException;

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
     * @throws MongoDBIOException on IO error with database
     */
    public DBCollection getCollection(String name) throws MongoDBException, MongoDBIOException;


    /**
     *  Drops a collection, the collections object, and the colletions indexes
     *
     * @param name Name of collection to drop
     * @return true if successful, false otherwise
     * @throws MongoDBException if error
     * @throws MongoDBIOException on IO error with database
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
     * @param dbOptions options to set for DB
     */
    public void setDBOptions(DBOptions dbOptions);

    public void resetDBOptions();

    public void close() throws Exception;

    public Doc eval(String function, Object... args) throws MongoDBException;
}