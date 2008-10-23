package org.mongo.driver.util;

import org.mongo.driver.MongoDoc;
import org.mongo.driver.MongoDBException;

/**
 * Interface for injecting primary keys into new objects
 */
public interface PKInjector {

    /**
     *  Returns the key used for injection
     * @return key
     */
    public String getKey();

    /**
     *  Injects the PK into the specified object
     *
     * @param doc Object to inject into
     * @return true if PK added, false if not
     * @throws MongoDBException if a problem with key
     */
    public boolean injectPK(MongoDoc doc) throws MongoDBException;
}
