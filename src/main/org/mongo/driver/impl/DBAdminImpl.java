package org.mongo.driver.impl;

import org.mongo.driver.admin.DBAdmin;
import org.mongo.driver.MongoDBException;

/**
 *  Implementation of the admin interface
 */
class DBAdminImpl implements DBAdmin {

    protected final DBImpl _myDB;

    protected int _profilingLevel = 0;
    
    DBAdminImpl(DBImpl db) {
        _myDB = db;
    }
    
    public int getProfilingLevel() {
        return _profilingLevel;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setProfilingLevel(LEVEL level) {
        switch(level)  {
            case OFF :
                _profilingLevel = 0;
                break;
            case SLOW_ONLY:
                _profilingLevel = 1;
                break;
            case ALL:
                _profilingLevel = 2;
                break;
        }

        // TODO - send to the database
    }
}
