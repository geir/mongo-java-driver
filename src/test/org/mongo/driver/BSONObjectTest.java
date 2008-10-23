package org.mongo.driver;

import org.testng.annotations.Test;
import org.mongo.driver.util.BSONObject;

import java.nio.ByteBuffer;

/**
 * User: geir
 * Date: Oct 10, 2008
 * Time: 7:30:15 AM
 */
public class BSONObjectTest {

    @Test
    public void testObjectEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        MongoDoc inner = new MongoDoc();

        inner.put("age", 41.2);
        inner.put("name", "geir");

        MongoDoc md = new MongoDoc();

        md.put("doc", inner);

        bo.serialize(md);

        MongoDoc md2 = bo.deserialize();

        inner = (MongoDoc) md2.get("doc");

        assert(((Number) inner.get("age")).doubleValue() == 41.2);
        assert(inner.get("name").equals("geir"));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        inner = (MongoDoc) md2.get("doc");

        assert(((Number) inner.get("age")).doubleValue() == 41.2);
        assert(inner.get("name").equals("geir"));
    }


    @Test
    public void testNumberEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        MongoDoc md = new MongoDoc();

        md.put("age", 41.2);

        bo.serialize(md);

        MongoDoc md2 = bo.deserialize();

        assert(((Number) md2.get("age")).doubleValue() == 41.2);

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(((Number) md2.get("age")).doubleValue() == 41.2);
    }


    @Test
    public void testStringEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        MongoDoc md = new MongoDoc();

        md.put("name", "geir");

        bo.serialize(md);

        MongoDoc md2 = bo.deserialize();

        assert(md2.get("name").equals("geir"));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("name").equals("geir"));
    }


    @Test
    public void testSerializeCSTR() throws Exception {

        ByteBuffer buff = ByteBuffer.allocate(1024);
        BSONObject bo = new BSONObject();

        assert(bo.serializeCSTR(buff, "name") == 5);

        buff.position(0);

        assert(bo.serializeCSTR(buff, "b") == 2);
    }



}
