import org.mongo.driver.impl.DBImpl;
import org.mongo.driver.MongoDoc;

val db = new DBImpl("scala");

val coll = db.getCollection("test", true);
coll.clear();

val doc = new MongoDoc();
doc.put("a", 1);
coll.insert(doc);

doc.put("a", 2);
coll.insert(doc);

val cur = coll.find();

while ( cur.hasMoreElements() ) {
   println(cur.nextElement());
}
