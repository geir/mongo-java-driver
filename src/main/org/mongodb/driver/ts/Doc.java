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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *  First run at an ordered, multi-entry "map"
 */
public class Doc extends LinkedHashMap<String, Object> {

    public Doc() {
    }

    public Doc(String key, Object value) {
        put(key, value);
    }

    public Doc(Doc doc) {
        this.putAll(doc);
    }

    public Doc(Map doc) {
        this.putAll(doc);
    }

    /**
     *  Adds a key/value pair to the document.  It will not
     *  replace the existing value for the key if it exists - it will
     *  add another
     * 
     * @param key key
     * @param value value to store
     * @return this document for ease of chaining
     */
    public Doc add(String key, Object value) {
        put(key, value);
        return this;
    }

    public void add(Doc doc) {
        this.putAll(doc);
    }

    public void add(Map map) {
        this.putAll(map);
    }

    /**
     *  Adds or replaces the value for a key.  (E.g. Map.put() semantics)
     *
     *  TODO : If there are multiple entries for this key, they will be removed (?)
     *  
     * @param key key
     * @param value value to store
     * @return old value, or null if not currently in Doc
     */
    public Object put(String key, Object value) {

        if (key == null) {
            throw new NullPointerException("Null key not allowed");
        }

        return super.put(key,value);
    }

    /**
     *  Returns the first object for a given key
     * 
     * @param key
     * @return
     */
    public Object get(String key) {
        if (key == null ) {
            throw new NullPointerException("Null key not allowed");
        }

        return super.get(key);
    }

    public int getInt(String key) {
        return (Integer) get(key);
    }

    public Doc getDoc(String key) {
        return (Doc) get(key);
    }

    public List<Duple> getList() {

        List<Duple> list = new ArrayList<Duple>();

        for (Map.Entry<String, Object> e : entrySet()) {
            list.add(new Duple(e.getKey(), e.getValue()));
        }

        return list;
    }
    
    public class Duple {

        Duple(Duple d) {
            if (d == null || d._key == null) {
                throw new NullPointerException("Null duple or duple key");
            }
            _key = d._key;
            _value = d._value;
        }

        Duple(String key, Object value) {
            _key = key;
            _value = value;
        }

        public String _key;
        public Object _value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (Map.Entry<String, Object> e : this.entrySet()) {

            sb.append(e.getKey());
            sb.append(": ");
            sb.append(e.getValue());
            sb.append(", ");
        }
        sb.append("}");

        return sb.toString();
    }
}
