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

import org.mongodb.driver.ts.DBCollection;
import org.mongodb.driver.ts.DBCursor;
import org.mongodb.driver.ts.DB;
import org.mongodb.driver.ts.Mongo;
import org.mongodb.driver.ts.Doc;

/**
 *  Simple example on how to use mongo driver from java
 */
public class MongoDemo {

    public static void main(String[] args) throws Exception {

        DB db = new Mongo().getDB("java");

        DBCollection coll = db.getCollection("test");
        coll.clear();

        Doc doc = new Doc();
        doc.put("a", 1);
        coll.insert(doc);

        doc.put("a", 2);
        coll.insert(doc);

        DBCursor cur = coll.find();

        for(Doc d : cur) {
            System.out.println("Doc : " + d);
        }
    }
}
