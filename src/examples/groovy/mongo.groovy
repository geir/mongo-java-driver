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

import org.mongodb.driver.dyn.Mongo

def mongo = new Mongo();

def db = mongo.getDB("groovy")

def coll = db.getCollection("test")
coll.clear()

for (i in 1..5) {
  coll.insert(["a" : i+1])
}

coll.find().each { i -> println i }


coll.find(["a" : 2]).each { i -> println i }
