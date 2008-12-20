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
package org.mongodb.mql;

import java.util.List;
import java.util.ArrayList;

/**
 *  Class to collect info about a query from the AST.
 */
public class QueryInfo {

    public enum QueryType { SELECT, UPDATE, DELETE, INSERT }
    
    private int _type;
    private int _limit;
    private int _skip;
    private String _collection_name="NOCOLL";
    private List<Field> _returnFieldList = new ArrayList<Field>();
    private List<Field> _setFieldList = new ArrayList<Field>();
    private String _whereClause = "";
    private List<Field> _orderByClauses = new ArrayList<Field>();
    private boolean _count;

    public QueryInfo() {
    }
    
    public void addSetField(Field f) {
        _setFieldList.add(f);
    }

    public List<Field> getSetFields() {
        return _setFieldList;
    }
    
    public void setType(int i) {
        _type = i;
    }

    public int getType() {
        return _type;
    }

    public String getWhereClause() {
        return _whereClause;
    }

    public void setWhereClause(String s) {
        _whereClause = s;
    }

    public void addOrderBy(Field f) {
        _orderByClauses.add(f);
    }

    public List<Field> getOrderByClauses() {
        return _orderByClauses;
    }

    public boolean isCount() {
        return _count;
    }

    public void setCount(boolean count) {
        _count = count;
    }

    public int getLimit() {
        return _limit;
    }

    public void setLimit(int limit) {
        _limit = limit;
    }

    public int getSkip() {
        return _skip;
    }

    public void setSkip(int skip) {
        _skip = skip;
    }

    public String getCollectionName() {
        return _collection_name;
    }

    public void setCollectioName(String collection_name) {
        _collection_name = collection_name;
    }

    public List<Field> getReturnFieldList() {
        return _returnFieldList;
    }

    public void addReturnField(Field f) {
        _returnFieldList.add(f);
    }

    public QueryType getQueryType() {

        switch(_type) {
            case MQLTreeConstants.JJTSELECT :
                return QueryType.SELECT;

            case MQLTreeConstants.JJTUPDATE :
                return QueryType.UPDATE;

            case MQLTreeConstants.JJTDELETE :
                return QueryType.DELETE;

            default:
                throw new RuntimeException("Unknown type");
        }
    }

    public static  class Field {
        public String _name;
        int _nameType;
        public String _value;
        int _valueType;

        public Field(String s, int t) {
            _name = s;
            _nameType = t;
        }

        public Field(String s, int a,  String v, int b) {
            _name = s;
            _nameType = a;
            _value = v;
            _valueType = b;
        }
    }
}
