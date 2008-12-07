package org.mongodb.driver.dyn.impl;

import org.mongodb.driver.ts.MongoDoc;

import java.util.Map;
import java.util.Iterator;

/**
 *
 */
public class MapIterator implements Iterator<Map> {

    protected final Iterator<MongoDoc> _mdi;

    protected MapIterator(Iterator<MongoDoc> mdi) {
        _mdi = mdi;
    }

    public boolean hasNext() {

        return _mdi.hasNext();
    }

    public Map next() {
        MongoDoc m = _mdi.next();

        if (m == null) {
            return null;
        }

        return m.getMap();
    }

    public void remove() {
        // TODO - intentional NOOP?
    }
}
