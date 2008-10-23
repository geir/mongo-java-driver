package org.mongo.driver.util;

import org.mongo.driver.MongoDoc;
import org.mongo.driver.DBObjectID;
import org.mongo.driver.MongoDBException;

/**
 * Primary Key injector that uses the conventions of
 * the 10gen Babble appserver
 */
public class JSPKInjector implements PKInjector {

    final static String KEY = "_id";

    public String getKey() {
        return KEY;
    }

    public boolean injectPK(MongoDoc doc) throws MongoDBException {

        if (doc.get(KEY) == null) {
            doc.put(KEY, new DBObjectID());
            return true;
        }
        return false;
    }
}
