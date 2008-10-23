package org.mongo.driver;

import java.util.Map;

/**
 * Typsafe selector
 */
public class MongoSelector extends MongoDoc {

    public MongoSelector() {
        super();
    }

    public MongoSelector(Map m) throws MongoDBException {
        super(m);
    }
    
    public MongoSelector(String s, Object o) throws MongoDBException {
        super(s, o);
    }

    protected void checkKey(String key) throws MongoDBException {

        if (key == null) {
            throw new MongoDBException("Error : key is null");
        }
    }

}
