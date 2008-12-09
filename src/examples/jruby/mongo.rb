#
#      Copyright (C) 2008 Geir Magnusson Jr
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

# To run from the driver top-level directory:
# CLASSPATH=mongo-driver.jar jruby src/examples/jruby/mongo.rb

require 'java'

# "ts" == "type safe". See also the use of "dyn" (dynamic) below.
mongo = org.mongodb.driver.ts.Mongo.new

db = mongo.get_db "jruby"

coll = db.get_collection "test"

coll.clear

10.times { |i|
  coll.insert 'a' => i + 1
}

coll.find.each { |row| puts row.to_s }


# now use Hash for objects and selectors

coll = db.get_collection "test2"
coll.clear

coll.insert 'b' => 1
coll.insert 'b' => 2

coll.find.each { |row| puts row.to_s }

# Try arrays, first with the existing "ts" (type safe) Mongo object, then with
# a "dyn" (dynamic) version.

coll.clear
coll.insert 'a' => [42, 7]
rows = coll.find.collect
# We need to call .get('a') becase rows[0] is a MongoDoc object
a = rows[0].get('a')
a.each { |val| puts val.to_s }

# Here we go with "dyn" (dynamic)
mongo = org.mongodb.driver.dyn.Mongo.new
db = mongo.get_db "jruby"
coll = db.get_collection "test"

coll.clear
coll.insert 'a' => [42, 7]
rows = coll.find.collect
# This time, rows[0] is a HashMap, which means we can use ['a']
a = rows[0]['a']
a.each { |val| puts val.to_s }
