package org.mongo.driver;

import org.mongo.driver.impl.Mongo;

import java.util.List;

public class MongoDriverTest {
    

    public static void main(String[] args) throws Exception {

        DB db = new Mongo().getDB("flarq");

        List<String> l = db.getCollectionNames();

        for (String s : l) {
            System.out.println(s);
        }

        DBCollection coll = db.getCollection("woog");
        
        DBCursor cur = coll.find();

        MongoDoc doc;
        while((doc = cur.getNextObject()) != null) {
            System.out.println(doc.toString());
        }

        System.out.println("------------");

        MongoSelector sel = new MongoSelector();

        sel.put("a", 1);

        DBQuery q = new DBQuery(sel);

        cur = coll.find(q);

        while((doc = cur.getNextObject()) != null) {
            System.out.println(doc.toString());
        }

        System.out.println("------------");
        
        doc = new MongoDoc();

        doc.put("a", 21);

        coll.insert(doc);

        cur = coll.find();

        while((doc = cur.getNextObject()) != null) {
            System.out.println(doc.toString());
        }

        System.out.println("------------");

        MongoDoc[] docs = new MongoDoc[5];

        for (int i = 17; i < 22; i++) {
            doc = new MongoDoc();
            doc.put("b", i);
            docs[i-17] = doc;
        }

        coll.insert(docs);

        cur = coll.find();

        while((doc = cur.getNextObject()) != null) {
            System.out.println(doc.toString());
        }

        System.out.println("------------");

        sel = new MongoSelector();

        sel.put("a", 21);

        coll.remove(sel);

        cur = coll.find();

        while((doc = cur.getNextObject()) != null) {
            System.out.println(doc.toString());
        }

        System.out.println("------------");

        coll.createIndex( new IndexInfo("a_1", "a"));
        coll.createIndex( new IndexInfo("a_2", "a"));
        coll.createIndex( new IndexInfo("a_3", "a"));
        coll.createIndex( new IndexInfo("a_4", "a"));

        coll.dropIndex("a_1");
        
        List<IndexInfo> list = coll.getIndexInformation();

        for (IndexInfo d : list) {
            System.out.println(d);
        }

        System.out.println("------------");
        
        coll = db.getCollection("foo");

        list = coll.getIndexInformation();

        for (IndexInfo d : list) {
            System.out.println(d);
        }

    }
}
