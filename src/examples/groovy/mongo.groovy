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
