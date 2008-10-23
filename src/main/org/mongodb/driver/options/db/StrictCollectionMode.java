package org.mongodb.driver.options.db;

import org.mongodb.driver.MongoSelector;
import org.mongodb.driver.options.impl.DBOptions;

/**
 * Puts the driver in a mode where the collections must exist to be gotten
 * and must not exist to be created.
 *
 * Default mode is permissive - in both of the above mentioned cases, it will
 * create if need be, and always return a collection.
 * 
 */
public class StrictCollectionMode extends DBOptions {

    public StrictCollectionMode() {
        add(this);
    }

    public MongoSelector getMongoSelector() {
        return new MongoSelector();
    }

}
