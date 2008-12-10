package org.mongodb.driver;

/**
 *  A MongoDB exception thrown when there's an I/O error
 */
public class MongoDBIOException extends RuntimeException {

    public MongoDBIOException(String msg, Throwable t) {
        super(msg, t);
    }
}
