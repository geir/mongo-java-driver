/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mongodb.driver.ts;

import org.mongodb.driver.MongoDBException;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Typesafe class for an MongoDoc that contains
 * only modifier actions.
 *
 * Currently supported modifier actions are :
 *
 * { $inc: { field:value } } increments field by the number value (if field is present in the object).
 *
 * { $set: { field:value } } sets field to the number value (if field is present in the object).
 *
 * Note that only the Number BSON datatype is supported right now.
 */
public class MongoModifier extends Doc {

    public MongoModifier() {
    }

    public MongoModifier(String key, Object val) throws MongoDBException {
        super(key, val);
    }

    public MongoModifier(Map m) throws MongoDBException {
        super(m);

        if (!valid()) {
            throw new MongoDBException("Error - map contains invalid keys");
        }
    }

    private static final Set<String> _MODIFIER_SET = new HashSet<String>() {
        {
            add("$inc");
            add("$set");
        }
    };

    public Object put(String key, Object val) {

        try {
            checkKey(key);
        }
        catch(MongoDBException e) {
            throw new RuntimeException("Error - invalid key ", e);
        }

        return super.put(key, val);
    }
    
    /**
     * Basic validation - ensure that any keys in the doc are suppored modifier verbs
     * 
     * @return true if valid doc, false otherwise
     */
    public boolean valid() {
        for (Duple d : this) {

            String key = d._key;
            
            if (!_MODIFIER_SET.contains(key)) {
                return false;
            }

            if (!(get(key) instanceof Doc)) {
                return false;
            }
        }
        return true;
    }

    protected void checkKey(String key) throws MongoDBException {

        if (key == null) {
            throw new MongoDBException("Error : key is null");
        }

        if (key.indexOf(".") != -1) {
            throw new MongoDBException("Error : key contains a '.'");
        }
    }
}
