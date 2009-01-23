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

import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.impl.MongoImpl;
import org.mongodb.driver.impl.DBImpl;

/**
 *  Mongo database server.  This is the top level class of the typsafe driver.
 * 
 */
public class Mongo extends MongoImpl {

    /**
     *   Creates a server instance connected to localhost on default port (27017)
     */
    public Mongo() {
        super();
    }

    /**
     * Creates a server instance connected to the specified host/IP
     * @param host host to connect to
     * @throws MongoDBException in case of problem
     */
    public Mongo(String host) throws MongoDBException {
        super(host);
    }

    /**
     * Creates a server instance connected to the specified host/IP and port
     * @param host host to connect to
     * @param port port number to connect to
     * @throws MongoDBException in case of problem
     */
    public Mongo(String host, int port) throws MongoDBException {
        super(host, port);
    }

    /**
     *   Gets the named DB object.  The DB will be created if it doesn't exist.
     * 
     * @param dbName name of database.  Will be created if it doesn't exist.
     * @return DB object
     * @throws MongoDBException in case of problem
     */
    public DB getDB(String dbName) throws MongoDBException {
        return new DBImpl(this, dbName);
    }
}
