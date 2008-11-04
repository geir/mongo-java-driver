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

package org.mongodb.driver;

import org.testng.annotations.Test;
import org.mongodb.driver.util.BSONObject;

import java.nio.ByteBuffer;
import java.util.Date;

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

    @Test
    public void testDate() throws Exception {

        BSONObject bo = new BSONObject();

        MongoDoc md = new MongoDoc();

        Date now = new Date();

        md.put("date", now);

        bo.serialize(md);

        MongoDoc md2 = bo.deserialize();

        assert(md2.get("date").equals(now));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("date").equals(now));
    }

    @Test
    public void testBoolean() throws Exception {

        BSONObject bo = new BSONObject();

        MongoDoc md = new MongoDoc();

        Date now = new Date();

        md.put("date", true);

        bo.serialize(md);

        MongoDoc md2 = bo.deserialize();

        assert(md2.get("date").equals(true));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("date").equals(true));

        md.put("date", false);

        bo = new BSONObject();  // TODO - why can't reuse?
        bo.serialize(md);

        md2 = bo.deserialize();

        assert(md2.get("date").equals(false));
    }

    @Test
    public void testNull() throws Exception {

        BSONObject bo = new BSONObject();

        MongoDoc md = new MongoDoc();

        Date now = new Date();

        md.put("date", (String) null);

        bo.serialize(md);

        MongoDoc md2 = bo.deserialize();

        assert(md2.get("date") == null);

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("date") == null);

    }



}
