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
package org.mongodb.driver.impl;

import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DBQuery;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.MongoDBException;
import org.mongodb.mql.QueryInfo;
import org.mongodb.mql.MQLTreeConstants;

import java.util.List;

/**
 *  Class to provide support for MQL in the mongo-java-driver.
 */
public class DriverQueryInfo extends QueryInfo {


    public DriverQueryInfo() {
    }

    /**
     *  Executes a query.  Note that if the MQL wasn't a "SELECT..." an exception will be thrown.
     *  You can check what kind of query this is via getQueryType()
     * 
     * @param db  database to run the query on
     * @return cursor of the result set
     * @throws MongoDBException in case of problem
     */
    public DBCursor execQuery(DB db) throws MongoDBException {

        if (getQueryType() != QueryType.SELECT) {
            throw new MongoDBException("Error - not a SELECT statement.  Cannot execute as query");
        }

        DBCollection coll = db.getCollection(getCollectionName());

        DBQuery q = new DBQuery();

        q.setQuery(getWhereClause());

        List<Field> retFields  = getReturnFieldList();

        if (retFields.size() > 0) {

            MongoSelector m = new MongoSelector();

            for (Field f : retFields) {
                m.put(f._name, "true");
            }
            q.setReturnFieldsSelector(m);
        }

        List<Field> orderBy = getOrderByClauses();

        if (orderBy.size() > 0) {

            MongoSelector m = new MongoSelector();

            for (Field f : orderBy) {
                m.put(f._name, f._value);
            }

            q.setOrderBy(m);
        }

        if (getLimit() > 0) {
            // TODO - need limit support in the driver
        }

        if (getSkip() > 0) {

            q.setNumberToSkip(getSkip());
        }

        if (isCount()) {
            // TODO - if a count, we should execute and return an iterator over a map that has one entry, the result
        }

        return coll.find(q);
    }


//    /**
//     * Exceutes the MQL as an update.
//     *
//     * @param db db to operate on
//     * @return number of objects updated
//     */
//    public int execUpdate(DB db) throws MongoDBException{
//
//        if (getQueryType() != QueryType.UPDATE) {
//            throw new MongoDBException("Error - not an UPDATE statement.  Cannot execute as query");
//        }
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(getDBVarName());
//        sb.append(".eval(  function() { ");
//
//        // do the find()
//
//        sb.append(" var cur = ");
//   //     sb.append(this.generateSelectString());
//        sb.append(";");
//
//        // now iterate and create our updater function
//
//        sb.append(" cur.forEach( function() {");
//
//        for (Field f : _setFieldList) {
//
//            // fixme
//
//            if (f._nameType == MQLTreeConstants.JJTPATH) {
//                sb.append("arguments[0].");
//            }
//
//            sb.append(f._name);
//            sb.append("=");
//
//            if (f._valueType == MQLTreeConstants.JJTPATH) {
//                sb.append("arguments[0].");
//            }
//
//            sb.append(f._value);
//            sb.append("; ");
//        }
//
//        sb.append(getDBVarName());
//        sb.append(".");
//        sb.append(_collection_name);
//        sb.append(".save(arguments[0]); ");
//
//        sb.append("})");
//
//        // close the dbeval function
//
//        sb.append("})");
//
//        return sb.toString();
//    }

}
