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

package org.mongodb.driver;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.fail;
import org.mongodb.driver.ts.MongoSelector;

/**
 * Tests for MongoModifier class
 *
 */
public class MongoSelectorTest {

    @Test
    public void verbotenKeyTest() throws MongoDBException {

        MongoSelector m = new MongoSelector();

        try {
            m.put(null, "hi");
            fail();
        }
        catch(Exception e) {
            //
        }
    }
}
