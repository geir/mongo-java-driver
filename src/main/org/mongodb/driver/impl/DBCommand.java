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

import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.DBQuery;
import org.mongodb.driver.ts.Doc;

/**
 *  A query for Mongo
 */
public class DBCommand extends DBQuery {

    public DBCommand(MongoSelector sel) {
        _querySelector = sel;
        setNumberToReturn(1); // tis required to be only 1 return, technally -1?
    }

    public Doc getCompleteQuery() throws MongoDBException {
        return _querySelector;
    }
}