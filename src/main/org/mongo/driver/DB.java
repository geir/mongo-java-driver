package org.mongo.driver;

import org.mongo.driver.admin.DBAdmin;
import org.mongo.driver.options.impl.MongoOptions;
import org.mongo.driver.options.impl.DBOptions;
import org.mongo.driver.options.impl.CollectionOptions;
import org.mongo.driver.options.impl.MongoOption;
import org.mongo.driver.options.db.StrictCollectionMode;

import java.util.List;

/**
 *  A Mongo database instance.
 * 
 */
public interface DB {

    /**
     *  Returns a list of the names of the collections  in this database
     *
     * @return List of collection names
     * @throws MongoDBException on error
     */
    public List<String> getCollectionNames() throws MongoDBException;

    /**
     *  Creates a new collection collection.  Like getCollection() relies upon the
     *  strict mode for creation
     *
     * @param name name of collection to create
     * @return collection
     * @throws MongoDBException if collection doesn't exist and in strict mode, or if
     *         there's a problem creating the collection
     */
    public DBCollection createCollection(String name) throws MongoDBException;

    /**
     *  Creates a collection with optional options.  Note that if options are passed in and not in strict
     *  mode, driver doesn't currently guarantee the options will be respected.
     *
     * @param name name of collection to create
     * @param options optinoal options for creation (e.g. CappedCollection)
     * @return collection
     * @throws MongoDBException if collection exists and in strict mode or an error creating collection
     */    
    public DBCollection createCollection(String name, CollectionOptions options) throws MongoDBException;


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
     */
    public DBCollection getCollection(String name) throws MongoDBException;


    /**
     *  Drops a collection, the collections object, and the colletions indexes
     *
     * @param name Name of collection to drop
     * @return true if successful, false otherwise
     * @throws MongoDBException if error
     */
    public boolean dropCollection(String name) throws MongoDBException;

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
     * @return
     */
    public DBOptions getDBOptions();

    /**
     * Sets the options for this database
     *
     * @param dbOptions
     */
    public void setDBOptions(DBOptions dbOptions);

    public void resetDBOptions();

    public void close() throws Exception;

   // DB.prototype.eval (and dbEval, which is same)
}
