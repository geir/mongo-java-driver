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

package org.mongodb.driver;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 *   <p>
 *   Representation of a document for Mongo.  Mongo stored "BSON" documents, which
 *   are a binary representation of aaugmented JSON document.
 *   </p>
 *
 *   <p>
 *   A Mongo doc is fundamentally a map, containing key-value pairs, and thus can be
 *   represented also as a HashMap.  However, there are some operations in which the
 *   key order is important in the BSON representation (e.g. any $cmd operation), and
 *   thus a MongoDoc also preserves insertion order.
 *   </p>
 */
public class MongoDoc implements Iterable<String> {

    final protected HashMap<String, Object> _map;

    /**
     * keep a list of keys in insertion order because some mongo features depend on document order.
     */
    protected List<String> _keyList = new ArrayList<String>();

    /**
     *   Guess at size of document.  Currently suspect :)
     */
    protected int _size = 0;

    public MongoDoc() {
        _map = new HashMap<String, Object>();
    }

    /**
     *  <p>
     *   Convenience constructor for  quick filters.  For example  :
     *  </p>
     *
     *  <blockquote><code>
     *  MongoDoc doc = new MongoDoc("a", 10);
     *  </code></blockquote>
     *
     * @param key key under which to store the associated value
     * @param value value to store
     * @throws MongoDBException if a problem with key
     */
    public MongoDoc(String key, Object value) throws MongoDBException {
        _map = new HashMap<String, Object>();
        put(key, value);
    }

    /**
     *  Convenience constructor.  Copies all elements of the map.
     *  Note that insertion order should be considered random.
     *
     * @param mapToCopy data to copy
     * @throws MongoDBException if a problem with key
     */
    public MongoDoc(Map mapToCopy) throws MongoDBException {
        _map = new HashMap<String, Object>(mapToCopy);

        // ensure we keep the keys

        _keyList.addAll(mapToCopy.keySet());

        // if someone handed us a map, convert to MongoDoc
        // TODO - fix this - too hackey

        for (String key : _map.keySet()) {

            Object val = get(key);

            if (val instanceof Map) {
                put(key, new MongoDoc((Map) val));
            }
        }
    }

    public void add(MongoDoc doc) {
        _map.putAll(doc._map);

        _keyList.addAll(doc._map.keySet());
    }
    
    public void put(String key, Number val)  throws MongoDBException{

        if (key == null) {
            return;
        }

        _size += 8;
        _put(key, val);
    }

    public void clear() {
        _map.clear();
        _keyList.clear();
    }
    
    public Object get(String key) {
        return _map.get(key);
    }

    public void put(String key, Object val) throws MongoDBException {
        _put(key, val);
    }

    protected void _put(String key, Object val) throws MongoDBException {

        checkKey(key);

        _keyList.add(key);
        _map.put(key, val);
    }

    public void put(String key, String val) throws MongoDBException {
        _put(key, val);
        _size += val.length() * 2;
    }

    public void put(String key, MongoDoc val) throws MongoDBException {
        _put(key, val);
        _size += val.getSize();
    }

    public void put(String key, DBObjectID val) throws MongoDBException {
        _put(key, val);
        _size += 12;
    }

    /**
     *   Sets an "array" type in the doc
     *
     * @param key key under which to store the associated value
     * @param value value to store
     * @throws MongoDBException if a problem with key
     */
    public void put(String key, List value) throws MongoDBException {

        checkKey(key);

        MongoDoc md = new MongoDoc();

        int i = 0;

        for (Object o : value) {
            md._put(String.valueOf(i++), o);
        }

        _put(key, md);
        _size += md.getSize();
    }

    /**
     * Returns a list of the keys in the order in which they were
     * inserted.  This is necessary as some Mongo features, like DB-commands
     * depend on an ordered documents
     *
     * @return List of key strings
     */
    public List<String> orderedKeyList() {
        return _keyList;
    }

    public Iterator<String> iterator() {
        return _map.keySet().iterator();
    }

    public int getSize() {
        return _size;
    }

    public int size() {
        return _map.size();
    }
    
    public String toString() {
        
        StringBuilder sb = new StringBuilder("{");

        for (String s : this) {
            sb.append(s);
            sb.append(": ");
            sb.append("[");
            sb.append(get(s).toString());
            sb.append("],");
        }

        sb.append("}");
        return sb.toString();
    }

    protected void checkKey(String key) throws MongoDBException {

        if (key == null) {
            throw new MongoDBException("Error : key is null");
        }

        if (key.startsWith("$")) {
            throw new MongoDBException("Error : key starts with $");
        }

        if (key.indexOf(".") != -1) {
            throw new MongoDBException("Error : key contains a '.'");
        }
    }
    
//    static final byte ARRAY = 4;
//    static final byte BINARY = 5;
//    static final byte UNDEFINED = 6;
//    static final byte OID = 7;
//    static final byte BOOLEAN = 8;
//    static final byte DATE = 9;
//    static final byte NULL = 10;
//    static final byte REGEX = 11;
//    static final byte REF = 12;
//    static final byte CODE = 13;
//    static final byte SYMBOL = 14;
//    static final byte CODE_W_SCOPE = 15;

}
