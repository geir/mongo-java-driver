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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the information for an index, which is comprised
 * of a index name and a set of field names
 *
 */
public class IndexInfo {

    protected String _indexName;
    protected List<String>_fieldList = new ArrayList<String>();
    protected String _collectionName;

    /**
     * Creates a new IndexInfo.  One or more fields are required
     * to create the index.
     *
     * @param indexName name of the index.  Suggest using $a_1 or like
     * @param fields one or more field names
     */
    public IndexInfo(String indexName, String... fields) {
        _indexName = indexName;
        _fieldList.addAll(Arrays.asList(fields));
    }

    /**
     *  Adds another field to the index info
     * @param field field
     */
    public void addField(String field) {
        _fieldList.add(field);
    }

    /**
     *  Gets the list of field for this index
     * @return list of fields
     */
    public List<String> getFields() {
        return _fieldList;
    }

    /**
     *  Name of the index
     * @return name of index
     */
    public String getIndexName() {
        return _indexName;
    }

    /**
     *  Sets the name of the index
     * @param indexName new name for index
     */
    public void setIndexName(String indexName) {
        _indexName = indexName;
    }

    /**
     *  Returns the name of the collection this index is on.
     * Note that this can't be set by user, but is supplied on
     *  return from a getIndexInformation() call on DBCollection
     * 
     *  @return name of collection
     */
    public String getCollectionName() {
        return _collectionName;
    }

    /**
     * Sets the collection name.  Used by the driver on returning Index information.
     * For any index creation, it will be ignored.
     *
     * @param collectionName name of colletion this index is in
     */
    public void setCollectionName(String collectionName) {
        _collectionName = collectionName;
    }
}
