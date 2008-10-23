package org.mongo.driver.options.collection;

import org.mongo.driver.options.impl.CollectionOptions;
import org.mongo.driver.options.impl.CollectionOption;
import org.mongo.driver.MongoSelector;

public class EmptyOptions extends CollectionOptions implements CollectionOption {

    public EmptyOptions() {
    }

    public MongoSelector getMongoSelector() {
        return  new MongoSelector();
    }
}
