package org.mongo.driver;

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
