package org.mongo.driver.options.impl;

import org.mongo.driver.MongoSelector;

/**
 * User: geir
 * Date: Oct 21, 2008
 * Time: 12:00:48 PM
 */
public interface MongoOption {
    public MongoSelector getMongoSelector();
}
