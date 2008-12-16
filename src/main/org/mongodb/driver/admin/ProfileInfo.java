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

import java.util.Date;

/**
 *  Holder for profiling information from a query
 */
public class ProfileInfo {

    protected final String _query;
    protected final long _timeInMillis;
    protected final Date _timestamp;

    public ProfileInfo(String q, long time, Date ts) {
        _query = q;
        _timeInMillis = time;
        _timestamp = new Date(ts.getTime());
    }

    public String getQuery() {
        return _query;
    }

    public long getTimeInMillis() {
        return _timeInMillis;
    }

    public Date getTimestamp() {
        return new Date(_timestamp.getTime());
    }
}
