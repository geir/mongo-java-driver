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
import org.mongodb.driver.ts.MongoModifier;
import org.mongodb.driver.ts.Doc;

/**
 * Tests for MongoModifier class
 *
 */
public class MongoModifierTest {

    @Test
    public void testBasic() throws MongoDBException {
        MongoModifier mm = new MongoModifier();

        mm.put("woog", "froobie");
        assert(!mm.valid());

        mm.clear();

        mm.put("$inc", "froop");
        assert(!mm.valid());

        mm.put("$inc", new Doc());
        assert(mm.valid());

        mm.put("$set", "asdasd");
        assert(!mm.valid());

        mm.put("$set", new Doc());
        assert(mm.valid());
    }

    @Test
    public void verbotenKeyTest() throws MongoDBException {

        MongoModifier m = new MongoModifier();

        try {
            m.put(null, "hi");
            fail();
        }
        catch(Exception e) {
            //
        }

        try {
            m.put("a.b", "hi");
            fail();
        }
        catch(Exception e) {
            //
        }
    }

}
