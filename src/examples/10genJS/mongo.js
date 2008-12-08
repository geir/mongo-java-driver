/**
*      Copyright (C) 2008 Geir Magnusson Jr
*  
*    Licensed under the Apache License, Version 2.0 (the "License");
*    you may not use this file except in compliance with the License.
*    You may obtain a copy of the License at
*  
*       http://www.apache.org/licenses/LICENSE-2.0
*  
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS,
*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*    See the License for the specific language governing permissions and
*    limitations under the License.
*/

mongo = javaCreate("org.mongodb.driver.ts.Mongo");

db = mongo.getDB("edshell");
coll = db.getCollection("test1");

coll.clear();

doc = javaCreate("org.mongodb.driver.ts.MongoDoc");
doc.put("a", 1);
coll.insert(doc);

doc.put("a", 2);
coll.insert(doc);

coll.find().forEach(function(x) { print("10gen JS : " + tojson(x));});

// and w/ native hashes

coll = db.getCollection("test2");
coll.clear();
coll.insert({"b":1});
coll.insert({"b":2});

coll.find().forEach(function(x) { print("10gen JS : " + tojson(x));});


// and again w/ dyn driver

mongo = javaCreate("org.mongodb.driver.dyn.Mongo");

db = mongo.getDB("edshell");
coll = db.getCollection("test1");

coll.clear();

coll.insert({"a": 3});
coll.insert({"a": 4});

coll.find().forEach(function(x) { print("10gen JS : " + tojson(x));});
