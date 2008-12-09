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

package org.mongodb.driver.ts;

import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.util.PKInjector;

import java.util.List;
import java.util.Map;

/**
 *  Collection of Mongo documents in a Mongo database.
 */
public interface DBCollection {

    /**
     *  Finds all objects in the collection
     *
     * @return cursor to get objects from the result set
     * @throws org.mongodb.driver.MongoDBException if somethign goes wrong
     */
    public DBCursor find() throws MongoDBException;

    /**
     *  Finds all objects in the collection
     *
     * @param selectorMap  selector expressed as a regular <code>java.util.Map</code>
     * @return cursor to get objects from the result set
     * @throws MongoDBException if somethign goes wrong
     */
    public DBCursor find(Map selectorMap) throws MongoDBException;

    /**
     *  Finds objects in the collection that match the specified query selector
     *
     * @param selector Selector to use to select objects 
     * @return cursor to get objects from the result set
     * @throws MongoDBException if somethign goes wrong
     */
    public DBCursor find(MongoSelector selector) throws MongoDBException;
    
    /**
     *  Finds objects in the collection that match the specified query
     *
     * @param query query to use to select objects
     * @return cursor to get objects from the result set
     * @throws MongoDBException if somethign goes wrong
     */
    public DBCursor find(DBQuery query) throws MongoDBException;

    /**
     * Inserts (saves) a single object to this collection.
     *
     * @param object the object to save
     * @return true always for now
     * @throws MongoDBException if something goes wrong
     */
    public boolean insert(Map object) throws MongoDBException;

    /**
     * Inserts (saves) a single object to this collection.
     *
     * @param object the object to save
     * @return true always for now
     * @throws MongoDBException if something goes wrong
     */
    public boolean insert(MongoDoc object) throws MongoDBException;

    /**
     * Inserts (saves) multiple objects to this collection.
     *
     * @param objects the objects to save
     * @return true always for now
     * @throws MongoDBException if something goes wrong
     */
    public boolean insert(MongoDoc[] objects) throws MongoDBException;

    /**
     * Removes all objects from the collection
     *
     *  @return true if successful
     *  @throws MongoDBException on error
      */
     public boolean clear() throws MongoDBException;

    /**
     * Removes objects from the database collection that match a specified selector.
     *
     *  @param selector Selector for objects to remove.  Cannot be null
     *  @return true if successful
     *  @throws MongoDBException on error
      */
     public boolean remove(MongoSelector selector) throws MongoDBException;

    /**
     * Performs an replace operation if the object is found, an insert otherwise.
     * Note that if there's a PKInjector, the object will be injected with a new PK,
     * and if replaced, will have the new PK
     *
     * @param selector search query for old object to replace
     * @param obj object with which to replace or insert
     * @return modified doc (will have new PK if injector present)
     * @throws MongoDBException if problem
     */
    public MongoDoc repsert(MongoSelector selector, MongoDoc obj) throws MongoDBException;

    /**
     *   Replaces objects found with the supplied object.  If a PK injector
     *   is present, a PK will be added to the new object
     *
     * @param sel Selector to select objects to be replaced
     * @param obj object to replace found objects with
     * @return true always
     * @throws MongoDBException on error
     */
    public boolean replace(MongoSelector sel , MongoDoc obj) throws MongoDBException;

    /**
     *   Modifies objects found with the modifiers in the supplied object.
     *
     * @param selector selector that specifies objects to match
     * @param modifierObj object that has modifier elements
     * @return true always
     * @throws MongoDBException if problem
     */
    public boolean modify(MongoSelector selector , MongoModifier modifierObj) throws MongoDBException;

    /**
     * Creates an index on a set of fields, if one does not already exist.
     * 
     * @param indexInfo an object with a key set of the fields desired for the index and the name
     * @return true always for now
     * @throws MongoDBException if error
     */
    public boolean createIndex(IndexInfo indexInfo) throws MongoDBException;

    /**
     * Drops an index.
     *
     * @param name name of index
     * @return true if index exists before drop, false if not
     * @throws MongoDBException on error
     */
    public boolean dropIndex(String name) throws MongoDBException;

    /**
     * Drops all indexes for the collection.
     *
     * @return true if successful
     * @throws MongoDBException on error
     */
    public boolean dropIndexes() throws MongoDBException;

    /**
     *  Returns a list of MongoDocs, each containing the information
     *  about an index :
     *      {
     *         name: string
     *         keys: mongodoc
     *      }
     *
     * @return list of mongodocs
     * @throws MongoDBException if problem
     */
    public List<IndexInfo> getIndexInformation() throws MongoDBException;

    /**
     * Returns the number of objects in the collection
     * 
     * @return number of objects
     * @throws MongoDBException in case of problem
     */
    public int getCount() throws MongoDBException;

    /**
     * Returns the number of objects in the collection
     * that match the supplied query selector
     *
     * @param selector selector to match objects for counting
     * @return number of objects
     * @throws MongoDBException in case of problem
     */
    public int getCount(MongoSelector selector) throws MongoDBException;

    /**
     * Returns the database this collection is a member of.
     * 
     * @return this collection's database
     */
    public DB getDB();

    /**
     * Returns the name of this collection.
     * 
     * @return  the name of this collection
     */
    public String getName();

    public DBCollectionOptions getOptions() throws MongoDBException;

    /**
     *  Sets the 'primary key' injector for this collection
     *  @param pki injector to use for each insert
     */
    public void setPKInjector(PKInjector pki);
}
