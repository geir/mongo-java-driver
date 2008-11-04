package org.mongodb.driver;

import org.mongodb.driver.impl.Mongo;

import javax.management.Query;
import java.util.Random;

/**
 * User: geir
 * Date: Oct 28, 2008
 * Time: 7:59:27 PM
 */
public class StressTest {

    Mongo _mongo;
    DB _db;
    DBCollection _coll;

    public StressTest() throws MongoDBException {
        _mongo = new Mongo();

        _db = _mongo.getDB("org_mongo_driver_StressTest");

        _coll = _db.getCollection("test");
    }   

    public void reset() throws MongoDBException {
        _db.dropCollection("test");
        _coll = _db.getCollection("test");
    }

    public void addObjects(int num, boolean withIndex) throws MongoDBException {

        if (withIndex) {
            _coll.createIndex(new IndexInfo("num_1", "num"));
        }
        
        long start = System.currentTimeMillis();

        MongoDoc d = new MongoDoc();
        d.put("name", "asdasdasda0da-0asd-0asd-a0sd-0as-da0s-as-d0koaspdoakspoda-09a-s0da-s0da-s0das");

        for (int i = 0; i < num; i++) {
            d.put("num", i);
            _coll.insert(d);
        }

        long end = System.currentTimeMillis();

        System.out.println("addObjects : " + 1.0 * num / (end - start) * 1000);
        
    }

    public void deleteLinear(int n) throws MongoDBException {

        long start = System.currentTimeMillis();

        MongoSelector m = new MongoSelector("num", 0);

        for (int i = 0; i < n; i++) {
            m.put("num", i);
            _coll.remove(m);
        }

        DBCursor cur = _coll.find(new DBQuery());
        cur.close();

        long end = System.currentTimeMillis();

        System.out.println("deleteLinear : n = " + n + " t = "+ (end-start) / 1000.0 + " sec : " + 1.0 * n / (end - start) * 1000);
    }

    public void deleteEveryOther(int n) throws MongoDBException {

        int i = 0;

        long start = System.currentTimeMillis();

        DBCursor cur = _coll.find();

        for (MongoDoc d : cur) {

            if ((d.getInt("num") % 2) == 0) {
                _coll.remove(new MongoSelector(d.getMap()));
                i++;
            }

            if (--n == 0) {
                break;
            }
        }

        cur.close();
        // do a query so we can be sure all has been deleted

        DBQuery q = new DBQuery(new MongoSelector(), null, 0, 1);

        cur = _coll.find(q);

        cur.close();
        
        long end = System.currentTimeMillis();

        System.out.println("deleteEveryOther : i = " + i + " t = "+ (end-start) / 1000.0 + " sec : " + 1.0 * i / (end - start) * 1000);
    }

    public void findRandomRanges(int range, int count) throws MongoDBException {

        Random r = new Random();

        long start = System.currentTimeMillis();

        for (int i=0; i < count; i++) {

            int num = r.nextInt(range);

            int st = r.nextInt(range - num);
            
            DBCursor cur = _coll.find(new DBQuery("this.num > " + st + " && this.num < " + (st + num)));

            int x = 0;
            for (MongoDoc d : cur) {
                x++;
            }
            assert(x == num);
        }

        long end = System.currentTimeMillis();

        System.out.println("findRandomRanges : range = " + range + " count = " + count + " t = "+ (end-start) / 1000.0 + " sec : " + 1.0 * count / (end - start) * 1000);
    }

    public static void main(String[] args) throws Exception {

        StressTest st = new StressTest();
//
//        st.reset();
//
//        st.addObjects(100000, false);
//        st.deleteLinear(5000);
//        st.deleteEveryOther(5000);
//
        st.reset();

        st.addObjects(100000, true);
//        st.findRandomRanges(5000, 100);
//        st.deleteLinear(50000);
        st.deleteEveryOther(5000);
    }
}
