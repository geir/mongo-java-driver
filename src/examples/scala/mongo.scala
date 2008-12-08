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

import org.mongodb.driver.dyn.Mongo;

val mongo = new Mongo();

val db = mongo.getDB("scala");

val coll = db.getCollection("test");
coll.clear();

val doc = new HashMap();
doc.put("a", 1);
coll.insert(doc);

doc.put("a", 2);
coll.insert(doc);

val cur = coll.find();

while ( cur.hasNext() ) {
   println(cur.next());
}
