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

import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.Doc;
import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.IndexInfo;
import org.mongodb.driver.MongoDBException;

import java.util.ArrayList;

/**
 *
 */
public class XGenPerfTests {

    Mongo _m = new Mongo();
    DB _db;

    public XGenPerfTests() throws MongoDBException {

        _db = _m.getDB("xgen_perf_tests");
    }


    public long singleInsertTest(Doc d, int count, String collName, boolean index) throws MongoDBException {

        DBCollection dbc = _db.getCollection(collName);

        dbc.clear();

        if (index) {
            dbc.createIndex(new IndexInfo("x_1", "x"));
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            d.put("x", i);
            dbc.insert(d);
        }

//        dbc.find().close();
        
        long end = System.currentTimeMillis();

        return end - start;
    }

    public static void main(String[] args) throws Exception {

        int count = 10000;

        XGenPerfTests xgpt = new XGenPerfTests();

        xgpt.doIt(count);
        xgpt.doIt(count);
        xgpt.doIt(count);
        xgpt.doIt(count);
        xgpt.doIt(count);
        xgpt.doIt(count);
        xgpt.doIt(count);
    }

    public void doIt(int count) throws MongoDBException {
        long time;
        Doc d;

        System.out.println("========================================== START ============================================");

        d = makeLargeDoc();
        time =  singleInsertTest(d, count, "singleInsertTest_withIndex_large", true);
        System.out.println("singleInsertTest_withIndex_large : " + 1.0 * count / (time) * 1000 + " time = " + time + " count = " + count);

        d = makeMediumDoc();
        time =  singleInsertTest(d, count, "singleInsertTest_withIndex_medium", true);
        System.out.println("singleInsertTest_withIndex_medium : " + 1.0 * count / (time) * 1000 + " time = " + time + " count = " + count);

        d = makeSmallDoc();
        time =  singleInsertTest(d, count, "singleInsertTest_withIndex_small", true);
        System.out.println("singleInsertTest_withIndex_small : " + 1.0 * count / (time) * 1000 + " time = " + time + " count = " + count);

        d = makeLargeDoc();
        time =  singleInsertTest(d, count, "singleInsertTestNoIndex_large", false);
        System.out.println("singleInsertTestNoIndex_large : " + 1.0 * count / (time) * 1000 + " time = " + time + " count = " + count);

        d = makeMediumDoc();
        time =  singleInsertTest(d, count, "singleInsertTestNoIndex_medium", false);
        System.out.println("singleInsertTestNoIndex_medium : " + 1.0 * count / (time) * 1000 + " time = " + time + " count = " + count);

        d = makeSmallDoc();
        time =  singleInsertTest(d, count, "singleInsertTestNoIndex_small", false);
        System.out.println("singleInsertTestNoIndex_small : " + 1.0 * count / (time) * 1000 + " time = " + time + " count = " + count);

        System.out.println("=========================================== END ===========================================");
        
    }

    public Doc makeSmallDoc() {
        return new Doc();
    }

    public Doc makeMediumDoc() {

        Doc d = new Doc();

        d.add("integer", 5);
        d.add("number", 5.05);
        d.add("boolean", Boolean.FALSE);
        d.add("array", new ArrayList<String>() {{ add("test"); add("benchmark"); }});

        return d;
    }


    public Doc makeLargeDoc() {

        Doc d = new Doc();

        d.add("pb_id", 2321232);
        d.add("base_url", "http://www.example.com/test-me");
        d.add("total_word_count", 6743);
        d.add("access_time", 1234915320);

        Doc mt = new Doc();

        mt.add("description", "i am a long description string");
        mt.add("author", "Holly man");
        mt.add("dynamically_created_meta_tag", "who know what");

        d.add("meta_tags", mt);

        mt = new Doc();

        mt.add("counted_tags", 3450);
        mt.add("no_of_js_attached",10);
        mt.add("no_of_images", 6);

        d.put("page_structure", mt);

        mt = new Doc();
        for (int i = 0; i < 280; i ++) {
            mt.add(Integer.toString(i), "10gen");
        }

        d.add("harvested_words", mt);

        return d;
    }
}
