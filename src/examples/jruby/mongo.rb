require 'java'

db = org.mongo.driver.impl.DBImpl.new("jruby")

coll = db.get_collection "test", true

coll.clear

doc = org.mongo.driver.MongoDoc.new

10.times { |i|
  doc["a"] = i + 1
  coll.insert doc
}

coll.find.each { |i| puts i.to_s }


# now use native dictionariies for objects and selectors

coll = db.get_collection "test2", true
coll.clear

coll.insert ":b" => 1
coll.insert ":b" => 2

coll.find.each { |i| puts i.to_s }
