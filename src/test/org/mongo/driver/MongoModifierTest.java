package org.mongo.driver;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.fail;

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

        mm.put("$inc", new MongoDoc());
        assert(mm.valid());

        mm.put("$set", "asdasd");
        assert(!mm.valid());

        mm.put("$set", new MongoDoc());
        assert(mm.valid());
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
    }

}
