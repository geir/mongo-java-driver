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

package org.mongodb.driver.options.collection;

import org.mongodb.driver.MongoSelector;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.options.impl.CollectionOptions;
import org.mongodb.driver.options.impl.CollectionOption;

/**
 * Limits a collection to a single extent, specified in bytes.  You can further
 * contrain the collection to a limited number of documents (or the extent, whichever
 * is smaller)
 */
public class CappedCollection extends CollectionOptions implements CollectionOption {

    final protected int _sizeInBytes;
    final protected int _maxNumDocuments;

    public CappedCollection(int sizeInBytes) {
        _sizeInBytes = sizeInBytes;
        _maxNumDocuments = 0;
        add(this);
    }

    public CappedCollection(int sizeInBytes, int maxNumDocuments) {
        _sizeInBytes = sizeInBytes;
        _maxNumDocuments = maxNumDocuments;
        add(this);
    }

    public MongoSelector getMongoSelector() {
        MongoSelector ms = new MongoSelector();

        try {
            ms.put("capped", Boolean.TRUE);
            ms.put("size", _sizeInBytes);
            ms.put("max", _maxNumDocuments);
        }
        catch(MongoDBException e) {
            // should never happen
            e.printStackTrace();
        }

        return ms;
    }
}
