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

package org.mongodb.driver.admin;

/**
 * Admin interface for a DB
 */
public interface DBAdmin {

    enum LEVEL {  OFF, SLOW_ONLY, ALL }

    /**
     * Gets the profiling level for the given database - all queries will
     * @return current profiling level
     */
    public int getProfilingLevel();

    /**
     * Sets the a new profiling level
     *
     * @param level new profiling level
     */
    public void setProfilingLevel(LEVEL level);
}
