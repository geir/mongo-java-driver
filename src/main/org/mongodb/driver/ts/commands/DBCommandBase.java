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
package org.mongodb.driver.ts.commands;

import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.Doc;
import org.mongodb.driver.MongoDBException;

/**
 *
 */
public abstract class DBCommandBase implements DBCommand{

    protected Doc _resultDoc;

    public abstract String getCommandString();

    public MongoSelector getSelector() {

        try {
            return new MongoSelector(getCommandString(), 1);
        }
        catch(MongoDBException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setResultDoc(Doc d) {
        _resultDoc = d;
    }

}
