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
import org.mongodb.driver.MongoDBIOException;

import java.util.Enumeration;

/**
 *  <p>
 *  Cursor for the result set of a Mongo query.
 *  </p>
 *
 *  <p>
 *  Note that this cursor reads data off the wire aggressively
 *  to allow query multiplexing on the same database connection,
 *  so callers must close() the cursor when complete, otherwise
 *  server-side leaks may occur.
 *  </p>
 * 
 */
public interface DBCursor extends Iterable<MongoDoc>, Enumeration {

    /**
     *  Returns the next object in the cursor.
     * 
     * @return next object on this cursor
     * @throws org.mongodb.driver.MongoDBException on error
     */
    public MongoDoc getNextObject() throws MongoDBException, MongoDBIOException;

    /**
     *  Closes the cursor, closing any server-side resources being used.
     *
     *  @throws MongoDBException in case of error
     */
    public void close() throws MongoDBException, MongoDBIOException;
}
