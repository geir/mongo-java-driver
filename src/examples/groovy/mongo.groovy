import org.mongo.driver.impl.DBImpl
import  org.mongo.driver.MongoDoc

def db = new DBImpl("groovy")

def coll = db.getCollection("test", true)

coll.clear()

def doc = new MongoDoc()

for (i in 1..5) {
  doc["a"] = i + 1
  coll.insert doc
}

coll.find().each { i -> println i }

// now use native map for objects and selectors

coll = db.getCollection("test2", true)

coll.clear()

coll.insert( ["b" : 1 ])
coll.insert( ["b" : 2 ])

coll.find().each { i -> println i }

coll.find(["b" : 2]).each { i -> println i }
