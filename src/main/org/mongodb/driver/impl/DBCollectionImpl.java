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

package org.mongodb.driver.impl;

import org.mongodb.driver.DBCollection;
import org.mongodb.driver.DB;
import org.mongodb.driver.DBCursor;
import org.mongodb.driver.MongoDoc;
import org.mongodb.driver.DBQuery;
import org.mongodb.driver.IndexInfo;
import org.mongodb.driver.MongoSelector;
import org.mongodb.driver.MongoModifier;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.options.impl.CollectionOptions;
import org.mongodb.driver.options.collection.EmptyOptions;
import org.mongodb.driver.options.collection.CappedCollection;
import org.mongodb.driver.options.collection.InitialExtent;
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

    protected DBCollectionImpl(DBImpl db, String collection) {
        _db = db;
        _collection = collection;
    }

    public DBCursor find(DBQuery query) throws MongoDBException {
        return _db.queryDB(_collection, query);
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

    public boolean insert(Map docMap) throws MongoDBException {
        return insert(new MongoDoc(docMap));
    }

    public boolean insert(MongoDoc doc) throws MongoDBException {
        return _db.insertIntoDB(_collection, doc);
    }

    public boolean insert(MongoDoc[] docs) throws MongoDBException {
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

    public MongoDoc repsert(MongoSelector selector, MongoDoc obj) throws MongoDBException {
        return _db.repsertInDB(_collection, selector, obj);
    }

     public boolean replace(MongoSelector selector , MongoDoc obj) throws MongoDBException {
         return _db.replaceInDB(_collection, selector, obj);
     }

    public boolean  modify(MongoSelector selector, MongoModifier modifierObj) throws MongoDBException{

        if (modifierObj == null) {
            throw new MongoDBException("no obj");
        }

        if (selector == null) {
            throw new MongoDBException("no selector");
        }

        if (!modifierObj.valid()) {
            throw new MongoDBException("Modifier object not valid");
        }

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

    public CollectionOptions getOptions() throws MongoDBException {

        DBCursor resp = _db.getCollectionsInfo(_collection);

        MongoDoc doc = resp.getNextObject();

        MongoDoc optionDoc = (MongoDoc) doc.get("options");

        CollectionOptions options = new EmptyOptions();
        
        if (optionDoc != null) {

            Double d = (Double) optionDoc.get("size");  // just because mongo does doubles...?

            int i = d != null ? d.intValue() : 0;

            if (i > 0) {
                options.add(new InitialExtent(i));
            }
            
            Boolean val = (Boolean) optionDoc.get("capped");

            if (val != null && val) {

                options.add(new CappedCollection(i));  // fix
            }
        }

        return options;
    }


    /**
     *  PKInjector
     */
    public void setPKInjector(PKInjector pki) {
    }
}
