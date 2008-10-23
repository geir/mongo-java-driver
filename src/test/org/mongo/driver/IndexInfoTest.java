package org.mongo.driver;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.mongo.driver.impl.Mongo;

import java.util.HashMap;
import java.util.Map;

public class IndexInfoTest {

    @Test
    public void testBasic() throws Exception {

        IndexInfo ii = new IndexInfo("woog", "a", "b");
        
        assert(ii.getFields().size() == 2);
        assert(ii.getIndexName().equals("woog"));
        assert(ii.getCollectionName() == null);

        ii.addField("c");
        assert(ii.getFields().size() == 3);
    }
}
