import org.mongo.driver.DBCollection;
import org.mongo.driver.DBCursor;
import org.mongo.driver.MongoDoc;
import org.mongo.driver.DB;
import org.mongo.driver.impl.Mongo;

/**
 *  Simple example on how to use mongo driver from java
 */
public class MongoDemo {

    public static void main(String[] args) throws Exception {

        DB db = new Mongo().getDB("java");

        DBCollection coll = db.getCollection("test");
        coll.clear();

        MongoDoc doc = new MongoDoc();
        doc.put("a", 1);
        coll.insert(doc);

        doc.put("a", 2);
        coll.insert(doc);

        DBCursor cur = coll.find();

        for(MongoDoc d : cur) {
            System.out.println("MongoDoc : " + d);
        }
    }
}
