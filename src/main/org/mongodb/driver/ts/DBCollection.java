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
     *  Finds all documents in the collection
     *
     * @return cursor to get documents from the result set
     * @throws MongoDBException if something goes wrong
     */
    public DBCursor find() throws MongoDBException;

    /**
     *  Returns the "first" document in the collection. YMMV with regards to "first'.
     *
     * @return document
     * @throws MongoDBException if something goes wrong
     */
    public Doc findOne() throws MongoDBException;

    /**
     *  Finds all documents in the collection
     *
     * @param selectorMap  selector expressed as a regular <code>java.util.Map</code>
     * @return cursor to get documents from the result set
     * @throws MongoDBException if something goes wrong
     */
    public DBCursor find(Map selectorMap) throws MongoDBException;

    /**
     *  Finds documents in the collection that match the specified query selector
     *
     * @param selector Selector to use to select documents
     * @return cursor to get documents from the result set
     * @throws MongoDBException if something goes wrong
     */
    public DBCursor find(MongoSelector selector) throws MongoDBException;

    /**
     *  Returns the first document in the collection that matches the
     *  specified selector
     *
     * @param selector - query used to select the document to return
     * @return document
     * @throws MongoDBException if something goes wrong
     */
    public Doc findOne(MongoSelector selector) throws MongoDBException;

    /**
     *  Finds documents in the collection that match the specified query
     *
     * @param query query to use to select documents
     * @return cursor to get documents from the result set
     * @throws MongoDBException if something goes wrong
     */
    public DBCursor find(DBQuery query) throws MongoDBException;

    /**
     *  Returns the first document in the collection that matches the
     *  specified selector
     *
     * @param query - query used to select the document to return
     * @return document
     * @throws MongoDBException if something goes wrong
     */
    public Doc findOne(DBQuery query) throws MongoDBException;

    /**
     *  Finds documents in the collection that match the specified query
     *
     * @param whereClause query to use to select documents
     * @return cursor to get documents from the result set
     * @throws MongoDBException if something goes wrong
     */
    public DBCursor find(String whereClause) throws MongoDBException;

    /**
     * Inserts (saves) a single document to this collection.
     *
     * @param document the document to save
     * @return true always for now
     * @throws MongoDBException if something goes wrong
     */
    public boolean insert(Map document) throws MongoDBException;

    /**
     * Inserts (saves) a single document to this collection.
     *
     * @param document the document to save
     * @return true always for now
     * @throws MongoDBException if something goes wrong
     */
    public boolean insert(Doc document) throws MongoDBException;

    /**
     * Inserts (saves) multiple documents to this collection.
     *
     * @param documents the documents to save
     * @return true always for now
     * @throws MongoDBException if something goes wrong
     */
    public boolean insert(Doc[] documents) throws MongoDBException;

    /**
     * Removes all documents from the collection
     *
     *  @return true if successful
     *  @throws MongoDBException on error
      */
     public boolean clear() throws MongoDBException;

    /**
     * Removes documents from the database collection that match a specified selector.
     *
     *  @param selector Selector for documents to remove.  Cannot be null
     *  @return true if successful
     *  @throws MongoDBException on error
      */
     public boolean remove(MongoSelector selector) throws MongoDBException;

    /**
     * Performs an replace operation if the document is found, an insert otherwise.
     * Note that if there's a PKInjector, the document will be injected with a new PK,
     * and if replaced, will have the new PK
     *
     * @param selector search query for old document to replace
     * @param obj document with which to replace or insert
     * @return modified doc (will have new PK if injector present)
     * @throws MongoDBException if problem
     */
    public Doc repsert(MongoSelector selector, Doc obj) throws MongoDBException;

    /**
     *   Replaces documents found with the supplied document.  If a PK injector
     *   is present, a PK will be added to the new document
     *
     * @param sel Selector to select documents to be replaced
     * @param obj document to replace found documents with
     * @return true always
     * @throws MongoDBException on error
     */
    public boolean replace(MongoSelector sel , Doc obj) throws MongoDBException;

    /**
     *   Modifies documents found with the modifiers in the supplied document.
     *
     * @param selector selector that specifies documents to match
     * @param modifierObj document that has modifier elements
     * @return true always
     * @throws MongoDBException if problem
     */
    public boolean modify(MongoSelector selector , Doc modifierObj) throws MongoDBException;

    /**
     * Creates an index on a set of fields, if one does not already exist.
     * 
     * @param indexInfo an document with a key set of the fields desired for the index and the name
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
     * Returns the number of documents in the collection
     * 
     * @return number of documents
     * @throws MongoDBException in case of problem
     */
    public int getCount() throws MongoDBException;

    /**
     * Returns the number of documents in the collection
     * that match the supplied query selector
     *
     * @param selector selector to match documents for counting
     * @return number of documents
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
