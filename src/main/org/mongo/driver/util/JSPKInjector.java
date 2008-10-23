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
