package org.mongodb.driver.dyn.impl;

import org.mongodb.driver.dyn.Collection;
import org.mongodb.driver.dyn.DB;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.util.PKInjector;
import org.mongodb.driver.impl.DBImpl;
import org.mongodb.driver.ts.options.DBCollectionOptions;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.MongoModifier;
import org.mongodb.driver.ts.IndexInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class CollectionImpl implements Collection {

    protected final DBCollection _coll;

    public CollectionImpl(DBCollection coll) {
        _coll = coll;
    }

    public Iterator<Map> find() throws MongoDBException {

        return iteratorFromCursor(_coll.find());
    }

    public Iterator find(Map selectorMap) throws MongoDBException {

        return iteratorFromCursor(_coll.find(selectorMap));
    }

    public Iterator find(String query) throws MongoDBException {

        return iteratorFromCursor(_coll.find(query));
    }

    public boolean insert(Map object) throws MongoDBException {
        return _coll.insert(object);
    }

    public boolean insert(Map[] objects) throws MongoDBException {

        MongoDoc[] mds = new MongoDoc[objects.length];

        int i = 0;
        for (Map m : objects) {
            mds[i++] = new MongoDoc(m);
        }

        return _coll.insert(mds);
    }

    public boolean clear() throws MongoDBException {
        return _coll.clear();
    }

    public boolean remove(Map selector) throws MongoDBException {

        return _coll.remove(new MongoSelector(selector));
    }

    public Map repsert(Map selector, Map obj) throws MongoDBException {

        MongoDoc m =  _coll.repsert(new MongoSelector(selector), new MongoDoc(obj));

        if (m == null) {
            return null;
        }

        return m.getMap();
    }

    public boolean replace(Map selector, Map obj) throws MongoDBException {

        return _coll.replace(new MongoSelector(selector), new MongoDoc(obj));
    }

    public boolean modify(Map selector, Map modifierObj) throws MongoDBException {

        return _coll.modify(new MongoSelector(selector), new MongoModifier(modifierObj));
    }

    public boolean createIndex(Map indexInfo) throws MongoDBException {

        Object indexName = indexInfo.get("name");

        if (indexName == null) {
            throw new MongoDBException("Error - indexname is null");
        }

        Object fields = indexInfo.get("fields");

        if (fields == null) {
            throw new MongoDBException("Error - fields is null");
        }

        IndexInfo ii = new IndexInfo(indexName.toString());
                        
        if (fields instanceof List) {

            for (Object o : (List) fields) {
                ii.addField(o.toString());
            }
        }

        return _coll.createIndex(ii);
    }

    public boolean dropIndex(String name) throws MongoDBException {

        return _coll.dropIndex(name);
    }

    public boolean dropIndexes() throws MongoDBException {

        return _coll.dropIndexes();
    }

    public List<Map> getIndexInformation() throws MongoDBException {

        List<IndexInfo> iil = _coll.getIndexInformation();

        List<Map> ml = new ArrayList<Map>();

        for (IndexInfo ii : iil) {

            Map<String, Object> m = new HashMap<String, Object>();

            m.put("name", ii.getIndexName());
            m.put("collection", ii.getCollectionName());
            m.put("fields", ii.getFields());

            ml.add(m);
        }

        return ml;
    }

    public int getCount() throws MongoDBException {

        return _coll.getCount();
    }

    public int getCount(Map selector) throws MongoDBException {

        return _coll.getCount(new MongoSelector(selector));
    }

    public DB getDB() {

        // TODO - fix - I hate this
        return new DynDBImpl((DBImpl)_coll.getDB());
    }

    public String getName() {
        return _coll.getName();
    }

    public Map getOptions() throws MongoDBException {

        DBCollectionOptions opts = _coll.getOptions();

        Map<String, Integer> m = new HashMap<String, Integer>();

        if (opts.isCapped()) {
            m.put("sizeLimt", opts.getCappedSizeLimit());

            if (opts.getCappedObjectMax() != DBCollectionOptions.DB_DEFAULT) {
                m.put("maxObjects", opts.getCappedObjectMax());
            }

            return m;
        }

        if (opts.getInitialExtent() != DBCollectionOptions.DB_DEFAULT) {
            m.put("initialExtent", opts.getInitialExtent());

            return m;
        }

        throw new MongoDBException("PROGRAMMER ERROR - there are collection options I don't support - " + opts);
    }

    protected Iterator<Map> iteratorFromCursor(DBCursor cur) throws MongoDBException {
        if (cur == null) {
            throw new MongoDBException("Error - received null cursor");
        }

        return new MapIterator(cur.iterator());
    }

    public void setPKInjector(PKInjector pki) {
        _coll.setPKInjector(pki);
    }

}
