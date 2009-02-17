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

/**
 *  First run at an ordered, multi-entry "map"
 */
public class Doc implements Iterable<Doc.Duple>{

    protected final List<Duple> _dupleList = new ArrayList<Duple>();

    public Doc() {
    }

    public Doc(String key, Object value) {
        add(key, value);
    }

    public Doc(Doc doc) {
        add(doc);
    }

    public Doc(Map doc) {
        add(doc);
    }

    /**
     *  Returns a map - last for a given key wins
     * @return Map form of this Doc
     */
    public Map getMap() {

        Map<String, Object> m = new HashMap<String, Object>();

        for (Duple d : _dupleList) {
            m.put(d._key, d._value);
        }
        return m;
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
        _dupleList.add(new Duple(key, value));
        return this;
    }

    public void add(Doc doc) {
        for(Duple d : doc._dupleList) {
            _dupleList.add(new Duple(d));
        }
    }

    public void add(Map map) {
        for(Object e : map.entrySet()) {
            Duple d = new Duple(((Map.Entry) e).getKey().toString(), ((Map.Entry) e).getValue());
            _dupleList.add(d);
        }
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

        for (Duple d : _dupleList) {
            if (d._key.equals(key)) {
                Object oldValue = d._value;
                d._value = value;
                return oldValue;
            }
        }
        _dupleList.add(new Duple(key, value));

        return null;
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

        for (Duple d : _dupleList) {
            if (d._key.equals(key)) {
                return d._value;
            }
        }

        return null;
    }

    public Object[] getAll(String key) {

        if (key == null ) {
            throw new NullPointerException("Null key not allowed");
        }

        List<Object> ol = new ArrayList<Object>();

        for (Duple d : _dupleList) {
            if (d._key.equals(key)) {
                ol.add(d._value);
            }
        }

        return ol.toArray();
    }

    public int getInt(String key) {
        return (Integer) get(key);
    }

    public Doc getDoc(String key) {
        return (Doc) get(key);
    }

    public int size() {
        return _dupleList.size();
    }

    public void clear() {
        _dupleList.clear();
    }

    public List<Duple> getList() {
        return _dupleList;
    }

    /**
     *  Key iterator
     * @return iterator over element in the document
     */
    public Iterator<Duple> iterator() {
        return _dupleList.iterator();
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

        for (Duple d : _dupleList) {
            sb.append(d._key);
            sb.append(": ");
            sb.append(d._value.toString());
            sb.append(", ");
        }
        sb.append("}");

        return sb.toString();
    }
}
