/**
*      Copyright (C) 2008 Geir Magnusson Jr
*  
*    Licensed under the Apache License, Version 2.0 (the "License");
*    you may not use this file except in compliance with the License.
*    You may obtain a copy of the License at
*  
*       http://www.apache.org/licenses/LICENSE-2.0
*  
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS,
*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*    See the License for the specific language governing permissions and
*    limitations under the License.
*/

package org.mongodb.driver.impl;

import org.mongodb.driver.admin.DBAdmin;
import org.mongodb.driver.admin.ProfileInfo;
import org.mongodb.driver.MongoSelector;
import org.mongodb.driver.MongoDoc;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.DBQuery;
import org.mongodb.driver.DBCursor;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 *  Implementation of the admin interface
 */
class DBAdminImpl implements DBAdmin {

    protected static final String _SYSTEM_PROFILE = "system.profile";
    protected static final String _QUERY_KEY = "info";
    protected static final String _TIMESTAMP_KEY = "ts";
    protected static final String _EXEC_TIME_KEY = "millis";


    protected final DBImpl _myDB;
    DBAdminImpl(DBImpl db) {
        _myDB = db;
    }

    public List<ProfileInfo> getProfilingInfo() throws MongoDBException {

        List<ProfileInfo> list = new ArrayList<ProfileInfo>();

        DBCursor cur = _myDB.queryDB(_SYSTEM_PROFILE, new DBQuery());

        for (MongoDoc doc : cur) {

            String q = (String) doc.get(_QUERY_KEY);
            long t = ((Double) doc.get(_EXEC_TIME_KEY)).longValue();
            Date d = (Date) doc.get(_TIMESTAMP_KEY);

            list.add(new ProfileInfo(q, t, d));
        }

        return list;
    }

    public LEVEL getProfilingLevel() throws MongoDBException {

        MongoSelector sel = new MongoSelector("profile", -1);

        MongoDoc md = _myDB.dbCommand(sel);

        Object o = md.get("was");

        if (o == null ||  !(o instanceof Number)) {
            throw new MongoDBException("Error - profiling level wasn't a valid value : " + o);
        }

        int level = ((Number) o).intValue();

        switch(level) {
            case 0 :
                return LEVEL.OFF;
            case 1 :
                return LEVEL.SLOW_ONLY;
            case 2 :
                return LEVEL.ALL;
            default :
                throw new MongoDBException ("PROGRAMMER ERROR : profiling level " + level + " erroneously not supported");
        }
    }

    public void setProfilingLevel(LEVEL profilingLevel) throws MongoDBException {

        int level;

        switch(profilingLevel)  {
            case OFF :
                level = 0;
                break;
            case SLOW_ONLY:
                level = 1;
                break;
            case ALL:
                level = 2;
                break;
            default :
                throw new MongoDBException ("PROGRAMMER ERROR : profiling level " + profilingLevel + " erroneously not supported");
        }

        MongoSelector sel = new MongoSelector("profile", level);

        MongoDoc md = _myDB.dbCommand(sel);

        Object o = md.get("ok");

        if (o == null || !(o instanceof Number)) {
            throw new MongoDBException ("Error - setting profile to level " + level + " failed.  DB response : " + md);
        }

        if (((Number)o).intValue() != 1) {
            throw new MongoDBException ("Error - setting profile to level " + level + " failed.  DB response : " + md);
        }
    }
}
