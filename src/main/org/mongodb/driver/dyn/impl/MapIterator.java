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

package org.mongodb.driver.dyn.impl;

import org.mongodb.driver.ts.Doc;

import java.util.Map;
import java.util.Iterator;

/**
 *
 */
public class MapIterator implements Iterator<Map>, Iterable<Map> {

    protected final Iterator<Doc> _mdi;

    protected MapIterator(Iterator<Doc> mdi) {
        _mdi = mdi;
    }

    public boolean hasNext() {

        return _mdi.hasNext();
    }

    public Map next() {
        Doc m = _mdi.next();

        if (m == null) {
            return null;
        }

        return m;
    }

    public void remove() {
        // TODO - intentional NOOP?
    }

    public Iterator<Map> iterator() {
        return this;
    }
}
