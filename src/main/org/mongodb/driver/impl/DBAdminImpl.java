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
