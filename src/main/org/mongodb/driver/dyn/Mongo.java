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

package org.mongodb.driver.dyn;

import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.MongoDBIOException;
import org.mongodb.driver.dyn.impl.DynDBImpl;
import org.mongodb.driver.impl.MongoImpl;

/**
 *  Mongo database server.  This is the top level class of the non-typesafe, dynamic driver.
 *
 *  TODO - keep track of the DBs that were handed out so we can do nice things to close later
 *
 */
public class Mongo extends MongoImpl {

    public Mongo() {
        super();
    }

    public Mongo(String host) throws MongoDBException {
        super(host);
    }

    public Mongo(String host, int port) throws MongoDBException {
        super(host, port);
    }

    public DB getDB(String dbName) throws MongoDBException, MongoDBIOException {

        return new DynDBImpl(this, dbName);
    }
}
