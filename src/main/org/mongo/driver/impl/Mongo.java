package org.mongo.driver.impl;

import org.mongo.driver.MongoDBException;
import org.mongo.driver.DB;
import org.mongo.driver.options.impl.DBOptions;

import java.net.InetSocketAddress;

/**
 *  Mongo database server.  This is the top level class of the driver.
 * 
 */
public class Mongo {

    public static final int DEFAULT_MONGO_PORT = 27017;

    protected DBOptions _options;

    protected InetSocketAddress _addr = new InetSocketAddress(DEFAULT_MONGO_PORT);

    public Mongo() {
    }

    public Mongo(String host) throws MongoDBException {
        this(host, DEFAULT_MONGO_PORT);
    }

    public Mongo(String host, int port) throws MongoDBException {
        try {
            _addr = new InetSocketAddress(host, port);
        }
        catch (IllegalArgumentException iae) {
            throw new MongoDBException("Invalid address : ",  iae);
        }
    }

    public DB getDB(String dbName) throws MongoDBException {

        return new DBImpl(this, dbName, _addr);
    }

    public boolean cloneDatabase(String from) {
        return false;
    }

    public boolean copyDatabase(String fromHost, String fromDB, String toDB) {
        return false;
    }
    
}
