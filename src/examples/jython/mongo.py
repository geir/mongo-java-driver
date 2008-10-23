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

