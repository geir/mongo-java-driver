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
