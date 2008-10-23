from org.mongo.driver.impl import DBImpl
from org.mongo.driver import MongoDoc

db = DBImpl("jython");

coll = db.getCollection("test1", 1);
coll.clear();

doc = MongoDoc();
doc.put("a", 1);
coll.insert(doc);

doc.put("a", 2);
coll.insert(doc);

cur = coll.find();

for i in cur:
    print i

# go native!  this only works in jython 2.5++

coll = db.getCollection("test2", 1);
coll.clear();

coll.insert({'b': 1});
coll.insert({'b': 2});

for i in coll.find():
    print i

