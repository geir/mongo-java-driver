package org.mongo.driver;

/**
 *  Base exception thrown by the Mongo DB API
 */
public class MongoDBException extends Exception {

    public MongoDBException() {
        super();
    }

    public MongoDBException(Throwable t) {
        super(t);
    }

    public MongoDBException(String msg) {
        super(msg);
    }

    public MongoDBException(String msg, Throwable t) {
        super(msg, t);
    }
}
