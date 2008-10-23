mongo = javaCreate("org.mongo.driver.Mongo");

db = mongo.getDB("edshell");
coll = db.getCollection("test1", true);

coll.clear();

doc = javaCreate("org.mongo.driver.MongoDoc");
doc.put("a", 1);
coll.insert(doc);

doc.put("a", 2);
coll.insert(doc);

coll.find().forEach(function(x) { print("10gen JS : " + x);});

// and w/ native hashes

coll = db.getCollection("test2", true);
coll.clear();
coll.insert({"b":1});
coll.insert({"b":2});

coll.find().forEach(function(x) { print("10gen JS : " + x);});
