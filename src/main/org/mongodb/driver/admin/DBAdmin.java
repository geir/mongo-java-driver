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

package org.mongodb.driver.admin;

import org.mongodb.driver.MongoDBException;

import java.util.List;

/**
 * Admin interface for a DB
 */
public interface DBAdmin {

    enum ProfileLevel {  OFF, SLOW_ONLY, ALL }

    /**
     * Gets the profiling level for the given database - all queries will
     * @return current profiling level
     * @throws MongoDBException on error
     */
    public ProfileLevel getProfilingLevel() throws MongoDBException;

    /**
     * Sets the a new profiling level
     *
     * @param level new profiling level
     * @throws MongoDBException on error
     */
    public void setProfilingLevel(ProfileLevel level) throws MongoDBException;

    /**
     *   Returns current profiling info from the DB
     *   @return list of profile info objects.  Each has the query, timestamp and time of execution.
     *   @throws MongoDBException in case of error
     */
    public List<ProfileInfo> getProfilingInfo() throws MongoDBException;


    /**
     *  Validates a named collection - examines 
     * 
     * @param collectionName name of collection to validate
     * @return true if collection valid
     * @throws MongoDBException if collection problematic
     */
    public boolean validateCollection(String collectionName) throws MongoDBException;
}
