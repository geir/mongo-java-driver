package org.mongo.driver;

import java.util.Enumeration;

/**
 *  <p>
 *  Cursor for the result set of a Mongo query.
 *  </p>
 *
 *  <p>
 *  Note that this cursor reads data off the wire aggressively
 *  to allow query multiplexing on the same database connection,
 *  so callers must close() the cursor when complete, otherwise
 *  server-side leaks may occur.
 *  </p>
 * 
 */
public interface DBCursor extends Iterable<MongoDoc>, Enumeration {

    /**
     *  Returns the next object in the cursor.
     * 
     * @return next object on this cursor
     * @throws MongoDBException on error
     */
    public MongoDoc getNextObject() throws MongoDBException;

    /**
     *  Closes the cursor, closing any server-side resources being used.
     *
     *  @throws MongoDBException in case of error
     */
    public void close() throws MongoDBException;
}
