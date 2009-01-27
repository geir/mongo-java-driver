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
import org.mongodb.driver.impl.connection.Connection;
import org.mongodb.driver.impl.connection.SimpleConnection;
import org.mongodb.driver.ts.options.DBOptions;

import java.net.InetSocketAddress;

/**
 *  Mongo database server.  This is the top level class of the driver.
 *
 */
public abstract class MongoImpl {

    public static final int DEFAULT_MONGO_PORT = 27017;

    protected DBOptions _options = null;

    protected InetSocketAddress _addr = new InetSocketAddress(DEFAULT_MONGO_PORT);

    protected final Connection _connection;

    protected MongoImpl() {
        _connection = new SimpleConnection(_addr);
    }

    protected MongoImpl(String host) throws MongoDBException {
        this(host, DEFAULT_MONGO_PORT);
    }

    protected MongoImpl(String host, int port) throws MongoDBException {
        try {
            _addr = new InetSocketAddress(host, port);
            _connection = new SimpleConnection(_addr);
        }
        catch (IllegalArgumentException iae) {
            throw new MongoDBException("Invalid address : ",  iae);
        }
    }

    public Connection getConnection() {
        return _connection;
    }

    public InetSocketAddress getServerAddress() {
        return _addr;
    }

    public boolean cloneDatabase(String from) {
        return false;
    }

    public boolean copyDatabase(String fromHost, String fromDB, String toDB) {
        return false;
    }
}
