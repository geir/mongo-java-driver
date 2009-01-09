package org.mongodb.driver;

/**
 *
 */
public class MongoDBQueryException extends MongoDBException{
    public MongoDBQueryException() {
        super();
    }

    public MongoDBQueryException(Throwable t) {
        super(t);
    }

    public MongoDBQueryException(String msg) {
        super(msg);
    }

    public MongoDBQueryException(String msg, Throwable t) {
        super(msg, t);
    }
}
