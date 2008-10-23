package org.mongo.driver;

import org.testng.annotations.Test;

import java.util.Map;
import java.util.HashMap;
import static org.testng.AssertJUnit.*;

public class MongoDocTest {

    @Test
    public void orderTest() throws MongoDBException {

        MongoDoc d = new MongoDoc();

        d.put("deleteIndexes", "foo");
        d.put("index", "bar");

        assert(d.size() == 2);
        assert(d.orderedKeyList().size() == 2);
        assert(d.orderedKeyList().get(0).equals("deleteIndexes"));
        assert(d.orderedKeyList().get(1).equals("index"));
    }

    @Test
    public void clearTest() throws MongoDBException {
        MongoDoc d= new MongoDoc();

        assert(d.size() == 0);
        assert(d.orderedKeyList().size() == 0);

        d.put("a", 1);

        assert(d.size() == 1);
        assert(d.orderedKeyList().size() == 1);

        d.clear();

        assert(d.size() == 0);
        assert(d.orderedKeyList().size() == 0);
    }

    @Test
    public void mapTest() throws MongoDBException {

        Map m = new HashMap();

        m.put("a", 1);
        m.put("b", 2);

        MongoDoc d = new MongoDoc(m);

        assert(d.size() == 2);
        assert(d.orderedKeyList().size() == 2);
    }

    @Test
    public void verbotenKeyTest() throws MongoDBException {

        MongoDoc m = new MongoDoc();

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

        try {
            m.put("$foo", "hi");
            fail();
        }
        catch(Exception e) {
            //
        }
    }
}
