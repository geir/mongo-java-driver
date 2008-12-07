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
