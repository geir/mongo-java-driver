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

package org.mongodb.driver.ts.options;

import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.ts.MongoSelector;

/**
 *   Representation of options for a DB collection.  Note that options cannot
 *   be set on an existing collection - they only can be specified at
 *   collection creation time.
 */
public class DBCollectionOptions {

    public final static int DB_DEFAULT = -1;
    protected boolean _isCapped = false;
    protected int _initialExtent = DB_DEFAULT;
    protected int _cappedObjectMax = DB_DEFAULT;

    protected boolean _readOnly;

    public boolean isDefault() {
        return !_isCapped
                && _initialExtent == DB_DEFAULT
                && _cappedObjectMax == DB_DEFAULT;
    }

    public boolean isCapped() {
        return _isCapped;
    }

    public boolean isReadOnly() {
        return _readOnly;
    }

    public void setReadOnly() throws MongoDBException {

        if (_readOnly) {
            throw new MongoDBException("Can't modify - readonly");
        }
        _readOnly = true;
    }

    /**
     * Sets the collection as capped, with a required sizeLimit setting as specified.
     * specified.
     * @param sizeLimit max number of bytes that will be stored in this capped table
     * @param cappedObjectMax optional limit on number of objects to store max (up to the
     *                        size limit of the table.  Pass DBCollectionOptions.DB_DEFAULT
     *                        if you wish to use default size
     * @return this instance, to allow option call chaining
     * @throws MongoDBException if the specified size < 0 or the cappedObjectMax < 0, or if
     *         the caller attempts to change the options for an already-existing collection
     */
    public DBCollectionOptions setCapped(int sizeLimit, int cappedObjectMax) throws MongoDBException {

        if (_readOnly) {
            throw new MongoDBException("Cannot modify options on an already existing collection");
        }

        if (sizeLimit < 0) {
            throw new MongoDBException("Specified capped table size limit must be > 0");
        }

        _isCapped = true;
        _initialExtent = sizeLimit;

        if (cappedObjectMax != DB_DEFAULT) {
            if (cappedObjectMax < 0) {
                throw new MongoDBException("Specified object max limit must be > 0");
            }
            _cappedObjectMax = cappedObjectMax;
        }

        return this;
    }

    /**
     * Sets the collection as capped, with a required sizeLimit setting as specified.
     * There will be no limit on the number of objects that will be stored (up to the
     * size limit)
     *
     * @param sizeLimit max number of bytes that will be stored in this capped table
     * @return this instance, to allow option call chaining
     * @throws MongoDBException if the specified size < 0,  or if
     *         the caller attempts to change the options for an already-existing collection
     */
    public DBCollectionOptions setCapped(int sizeLimit) throws MongoDBException {

        if (_readOnly) {
            throw new MongoDBException("Cannot modify options on an already existing collection");
        }

        if (sizeLimit < 0) {
            throw new MongoDBException("Specified capped table size limit must be > 0");
        }

        _isCapped = true;
        _initialExtent = sizeLimit;
        _cappedObjectMax = DB_DEFAULT;

        return this;
    }

    /**
     * Sets the initial extent for a collection.  This is useful if you know you will have a limited
     * size collection and want to save space.  There's no harm in setting this - this is not the
     * same as capping.
     *
     * @param size in bytes for the initial extent
     * @return this object for chaining
     * @throws MongoDBException if the specified size < 0,  or if
     *         the caller attempts to change the options for an already-existing collection
     */
    public DBCollectionOptions setInitialExtent(int size) throws MongoDBException {
        if (_readOnly) {
            throw new MongoDBException("Cannot modify options on an already existing collection");
        }

        if (size < 0) {
            throw new MongoDBException("Specified initial extent must be > 0");
        }

        _initialExtent = size;

        return this;
    }

    public int getInitialExtent() {
        return _initialExtent;
    }

    public int getCappedSizeLimit() {
        return _initialExtent;
    }

    public int getCappedObjectMax() {
        return _cappedObjectMax;
    }

    public MongoSelector getSelector() {
        MongoSelector ms = new MongoSelector();

        if (_isCapped) {

            ms.put("capped", Boolean.TRUE);
            ms.put("size", _initialExtent);

            if (_cappedObjectMax != DB_DEFAULT) {
                ms.put("max", _cappedObjectMax);
            }
        }
        else {
            ms.put("size", _initialExtent);
        }

        return ms;
    }
}
