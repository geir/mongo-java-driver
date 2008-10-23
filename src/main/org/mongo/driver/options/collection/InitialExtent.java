package org.mongo.driver.options.collection;

import org.mongo.driver.options.impl.CollectionOptions;
import org.mongo.driver.options.impl.CollectionOption;
import org.mongo.driver.MongoSelector;
import org.mongo.driver.MongoDBException;

/**
 *   Sets the intial extent of the collection.  Only useful in the case of
 *   a collection being known to be small and limited in size - this should
 *   then be used to set the size small to save space.
 *
 *   There is no real harm in making it too small - the db will expand storage
 *   for a collection as needed (see Capping)
 */
public class InitialExtent extends CollectionOptions implements CollectionOption {

    final protected int _sizeInBytes;

    public InitialExtent(int sizeInBytes) {
        _sizeInBytes = sizeInBytes;
        add(this);
    }

    public int getSizeInBytes() {
        return _sizeInBytes;
    }

    public MongoSelector getMongoSelector() {
        MongoSelector ms = new MongoSelector();

        try {
            ms.put("size", _sizeInBytes);
        }
        catch(MongoDBException e) {
            // should never happen
            e.printStackTrace();
        }

        return ms;
    }
}