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

package org.mongodb.driver.ts.options;

/**
 * Represents the options for a Mongo Database
 */
public class DBOptions {
    private boolean _strictCollectionMode = false;

    /**
     *   Strict collection mode doesn't allow creation of collections if the collection
     *   already exists, and limits getting collections to only those that exist.
     *
     * @return true if in strict collection mode
     */
    public boolean isStrictCollectionMode() {
        return _strictCollectionMode;
    }

    /**
     *   Sets strict collection mode.
     * @param strictCollectionMode true if strict mode, false otherwise
     * @return this object for setter chaining
     */
    public DBOptions setStrictCollectionMode(boolean strictCollectionMode) {
        _strictCollectionMode = strictCollectionMode;
        return this;
    }
}
