package org.mongo.driver;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.fail;

/**
 * Tests for MongoModifier class
 *
 */
public class MongoSelectorTest {


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
