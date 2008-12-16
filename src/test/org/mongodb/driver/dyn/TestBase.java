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

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class TestBase {

    int cursorCount(Iterator<Map> c) {

        int i = 0;
        while(c.hasNext()) {
            c.next();
            i++;
        }
        return i;
    }

    Map<String, Object> newMap(String key, Object val) { 

        Map<String,Object> m = new HashMap<String, Object>();

        m.put(key, val);

        return m;
    }
}
