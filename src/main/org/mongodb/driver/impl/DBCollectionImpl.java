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

import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBQuery;
import org.mongodb.driver.ts.IndexInfo;
import org.mongodb.driver.ts.Doc;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.util.PKInjector;

import java.util.List;
import java.util.Map;


/**
 * Implements the DBCollection interface.
 * 
 */
class DBCollectionImpl implements DBCollection {

    protected final DBImpl _db;
    protected final String _collection;
    protected PKInjector _pkInjector = null;

    protected DBCollectionImpl(DBImpl db, String collection) {
        _db = db;
        _collection = collection;
    }

    public DBCursor find(String query) throws MongoDBException {
        return _db.queryDB(_collection, new DBQuery(query));
    }

    public DBCursor find(DBQuery query) throws MongoDBException {
        return _db.queryDB(_collection, query);
    }

    public Doc findOne(DBQuery query) throws MongoDBException {

        DBCursor c = find(query);

        if (c == null) {
            return null;
        }

        try {
            return c.hasMoreElements() ? c.getNextObject() : null;
        }
        finally {
            c.close();
        }
    }

    public Doc findOne(MongoSelector selector) throws MongoDBException {
        return findOne(new DBQuery(selector, null, 0, 1));
    }


    public DBCursor find(MongoSelector selector) throws MongoDBException {
        DBQuery q = new DBQuery(selector);

        return find(q);
    }

    public DBCursor find(Map selectorMap) throws MongoDBException {
        return find(new MongoSelector(selectorMap));
   }

    public DBCursor find() throws MongoDBException {
        return find(new DBQuery());
    }

    public Doc findOne() throws MongoDBException {
        return findOne(new DBQuery(new MongoSelector(), null, 0, 1));
    }

    public boolean insert(Map docMap) throws MongoDBException {

        return insert(new Doc(docMap));
    }

    public boolean insert(Doc doc) throws MongoDBException {

        if (_pkInjector != null) {
            _pkInjector.injectPK(doc);
        }
        
        return _db.insertIntoDB(_collection, doc);
    }

    public boolean insert(Doc[] docs) throws MongoDBException {

        if (_pkInjector != null) {
            for (Doc doc : docs) {
                _pkInjector.injectPK(doc);
            }
        }

        return _db.insertIntoDB(_collection, docs);
    }

    public boolean clear() throws MongoDBException {
        return remove(new MongoSelector());
    }

    public boolean remove(MongoSelector selector) throws MongoDBException {

        if (selector == null) {
            throw new MongoDBException("Selector is null.");
        }

        return _db.removeFromDB(_collection, selector);
    }

    public Doc repsert(MongoSelector selector, Doc obj) throws MongoDBException {
        return _db.repsertInDB(_collection, selector, obj);
    }

     public boolean replace(MongoSelector selector , Doc obj) throws MongoDBException {
         return _db.replaceInDB(_collection, selector, obj);
     }

    public boolean  modify(MongoSelector selector, Doc modifierObj) throws MongoDBException{

        if (modifierObj == null) {
            throw new MongoDBException("no obj");
        }

        if (selector == null) {
            throw new MongoDBException("no selector");
        }
//
//        if (!modifierObj.valid()) {
//            throw new MongoDBException("Modifier object not valid");
//        }

        return _db.modifyInDB(_collection, selector, modifierObj);
    }

    public boolean createIndex(IndexInfo info) throws MongoDBException {
        return _db.createIndex(_collection, info);
    }

    public boolean dropIndex(String name) throws MongoDBException {
        return _db.dropIndex(_collection, name);
    }

    public boolean dropIndexes() throws MongoDBException {

        List<IndexInfo> indexes = getIndexInformation();

        for (IndexInfo ii : indexes) {
            _db.dropIndex(_collection, ii.getIndexName());
        }

        return true;
    }

    public List<IndexInfo> getIndexInformation() throws MongoDBException {
        return _db.getIndexInformation(_collection);
    }

    public DB getDB() {
        return _db;
    }

    public String getName() {
        return _collection;
    }

    public int getCount(MongoSelector selector) throws MongoDBException {
        return _db.getCount(_collection, selector);
    }

    public int getCount() throws MongoDBException {
        return _db.getCount(_collection, new MongoSelector());
    }

    public DBCollectionOptions getOptions() throws MongoDBException {

        DBCursor resp = _db.getCollectionsInfo(_collection);

        Doc doc = resp.getNextObject();

        Doc optionDoc = (Doc) doc.get("options");

        DBCollectionOptions options = new DBCollectionOptions();

        if (optionDoc != null) {

            Integer ii = (Integer) optionDoc.get("size");  // just because mongo does doubles...?

            int i = ii != null ? ii : 0;

            if (i > 0) {
                options.setInitialExtent(i);
            }

            Boolean val = (Boolean) optionDoc.get("capped");

            if (val != null && val) {

                ii = (Integer) optionDoc.get("max");

                int maxObj = ii != null ? ii : 0;

                if (maxObj > 0) {
                    options.setCapped(i, maxObj);
                }
                else {
                    options.setCapped(i);
                }
            }
        }

        options.setReadOnly();  // lock it so user isn't tempted to try to change as it's pointless
        
        return options;
    }

    /**
     *  Allows a PKInjector to be set for this collection
     * 
     *  @param pki injector
     */
    public void setPKInjector(PKInjector pki) {
        _pkInjector = pki;
    }
}
