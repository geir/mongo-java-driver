importPackage(org.mongo.driver.impl);
importPackage(org.mongo.driver);

db = new DBImpl("rhino");

coll = db.getCollection("test1", true);
coll.clear();

doc = new MongoDoc();
doc.put("a", 1);
coll.insert(doc);

doc.put("a", 2);
coll.insert(doc);

cur = coll.find();
for (i in Iterator(cur)) print(i);


//
// rhino is too decrepit and retrograde to just deal with lists and maps natively (Why, Norris Boyd?  Why???
// They've only been around since what, Java 1.2?)
// 
// so the following doesn't work

//coll = db.getCollection("test2", true);
//coll.clear();
//
//coll.insert({b:1});
//coll.insert({b:2});
//
//cur = coll.find();
//for (i in Iterator(cur)) print(i);
