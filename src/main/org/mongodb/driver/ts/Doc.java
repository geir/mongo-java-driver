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

import java.util.Map;
import java.util.LinkedHashMap;

/**
 *  First run at an ordered, multi-entry "map"
 */
public class Doc extends LinkedHashMap<String, Object> {

    public Doc() {
    }

    /**
     *  CTOR to initialize Doc w/ a key/value pair
     * _
     * @param key key for storing value
     * @param value value for given key
     */
    public Doc(String key, Object value) {
        _put(key, value);
    }

    /**
     *  CTOR to initialize Doc w/ contents of another Doc
     *
     * @param doc doc to copy key/value pairs from
     */
    public Doc(Doc doc) {
        _putAll(doc);
    }

    /**
     *  CTOR to initialize Doc w/ contents from a Map
     *
     * @param map doc to copy key/value pairs from
     */
    public Doc(Map<String, Object> map) {
        _putAll(map);
    }

    private Object _put(String key, Object val) {
        return put(key, val);
    }

    private  void _putAll(Map<String, Object> map) {
        putAll(map);
    }

    /**
     *  Adds a key/value pair to the document.  Useful for chainging.
     * 
     * @param key key
     * @param value value to store
     * @return this document for ease of chaining
     */
    public Doc add(String key, Object value) {
        put(key, value);
        return this;
    }

    public Doc add(Doc doc) {
        this.putAll(doc);
        return this;
    }

    public Doc add(Map<String, Object> map) {
        this.putAll(map);
        return this;
    }

    /**
     *  Adds or replaces the value for a key.  (E.g. Map.put() semantics)
     *     *  
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
     *  Returns the  object for a given key
     * 
     * @param key key whose associated value is to be returned
     * @return value associated with specified key
     */
    public Object get(String key) {
        if (key == null) {
            throw new NullPointerException("Null key not allowed");
        }

        return super.get(key);
    }

    /**
     *  Convenience method to return the object for a given key as an integer.
     *  Note that the implementation is simple - you'll get a ClassCastException
     *  if the stored item isn't an integer
     *
     *  TODO - make this more helpful
     *
     * @param key key whose associated value is to be returned
     * @return value associated with specified key
     */
    public int getInt(String key) {
        return (Integer) get(key);
    }

    /**
     *  Convenience method to return the object for a given key as a string.
     *
     * @param key key whose associated value is to be returned
     * @return value associated with specified key
     */
    public String getString(String key) {

        Object o = get(key);

        return o == null?  null : o.toString();
    }

    /**
     *  Convenience method to return the object for a given key as a Doc.
     *  Note that the implementation is simple - you'll get a ClassCastException
     *  if the stored item isn't a Doc
     *
     *  TODO - make this more helpful
     *
     * @param key key whose associated value is to be returned
     * @return value associated with specified key
     */
    public Doc getDoc(String key) {
        return (Doc) get(key);
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
