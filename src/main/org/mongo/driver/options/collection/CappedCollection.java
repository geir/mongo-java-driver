package org.mongo.driver.options.collection;

import org.mongo.driver.MongoSelector;
import org.mongo.driver.MongoDBException;
import org.mongo.driver.options.impl.CollectionOptions;
import org.mongo.driver.options.impl.CollectionOption;

/**
 * Limits a collection to a single extent, specified in bytes.  You can further
 * contrain the collection to a limited number of documents (or the extent, whichever
 * is smaller)
 */
public class CappedCollection extends CollectionOptions implements CollectionOption {

    final protected int _sizeInBytes;
    final protected int _maxNumDocuments;

    public CappedCollection(int sizeInBytes) {
        _sizeInBytes = sizeInBytes;
        _maxNumDocuments = 0;
        add(this);
    }

    public CappedCollection(int sizeInBytes, int maxNumDocuments) {
        _sizeInBytes = sizeInBytes;
        _maxNumDocuments = maxNumDocuments;
        add(this);
    }

    public MongoSelector getMongoSelector() {
        MongoSelector ms = new MongoSelector();

        try {
            ms.put("capped", Boolean.TRUE);
            ms.put("size", _sizeInBytes);
            ms.put("max", _maxNumDocuments);
        }
        catch(MongoDBException e) {
            // should never happen
            e.printStackTrace();
        }

        return ms;
    }
}
