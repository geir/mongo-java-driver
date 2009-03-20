/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mongodb.driver;

import org.testng.annotations.Test;
import org.mongodb.driver.util.BSONObject;
import org.mongodb.driver.util.BSONObjectCallback;
import org.mongodb.driver.util.types.BabbleOID;
import org.mongodb.driver.util.types.BSONRef;
import org.mongodb.driver.util.types.BSONSymbol;
import org.mongodb.driver.util.types.BSONBytes;
import org.mongodb.driver.ts.Doc;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Tests for serialization and de-serialization
 */
public class BSONObjectTest {

    @Test
    public void testObjectEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc inner = new Doc();

        inner.put("age", 41.2);
        inner.put("name", "geir");

        Doc md = new Doc();

        md.put("doc", inner);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        inner = (Doc) md2.get("doc");

        assert(((Number) inner.get("age")).doubleValue() == 41.2);
        assert(inner.get("name").equals("geir"));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        inner = (Doc) md2.get("doc");

        assert(((Number) inner.get("age")).doubleValue() == 41.2);
        assert(inner.get("name").equals("geir"));
    }

    @Test
    public void testBSOBBytesEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc inner = new Doc();

        inner.put("age", 41.2);
        inner.put("name", "geir");

        bo.serialize(inner);

        BSONBytes bb = new BSONBytes(bo.toArray());

        Doc md = new Doc();
        md.put("doc", bb);

        bo = new BSONObject();

        bo.serialize(md);                
        Doc md2 = bo.deserialize();

        inner = (Doc) md2.get("doc");

        assert(((Number) inner.get("age")).doubleValue() == 41.2);
        assert(inner.get("name").equals("geir"));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        inner = (Doc) md2.get("doc");

        assert(((Number) inner.get("age")).doubleValue() == 41.2);
        assert(inner.get("name").equals("geir"));
    }

    @Test
    public void testBSOBBytesDecoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc inner = new Doc();

        inner.put("age", 41.2);
        inner.put("name", "geir");

        Doc md = new Doc();
        md.put("doc", inner);

        bo.serialize(md);

        bo.setBSONObjectCallback(new BSONObjectCallback() {
            public boolean deserializeObjectAsBSON(String key) {
                return "doc".equals(key);
            }
        });

        Doc md2 = bo.deserialize();

        Object o = md2.get("doc");

        assert(o instanceof BSONBytes);

        bo = new BSONObject();

        Doc out = bo.deserialize(((BSONBytes)o).getBytes());

        assert(((Number) out.get("age")).doubleValue() == 41.2);
        assert(out.get("name").equals("geir"));
    }


    @Test
    public void testNumberEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        md.put("age", 41.2);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        assert(((Number) md2.get("age")).doubleValue() == 41.2);

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(((Number) md2.get("age")).doubleValue() == 41.2);
    }

    @Test
    public void testIntegerEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        md.put("age", 40);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        assert(((Integer) md2.get("age")) == 40);

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(((Integer) md2.get("age")) == 40);
    }

    @Test
    public void testCodeEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        String code = "function() { return this.a;}";

        md.put("where", code);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        assert(md2.get("where").equals(code));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("where").equals(code));
    }

    @Test
    public void testSymbolEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        BSONSymbol bs = new BSONSymbol("this is stupid");

        md.put("bs", bs);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        BSONSymbol bstoo = (BSONSymbol) md2.get("bs");

        assert(bstoo.getSymbol().equals(bs.getSymbol()));
    }


    @Test
    public void testStringEncoding() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        md.put("name", "geir");

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        assert(md2.get("name").equals("geir"));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("name").equals("geir"));
    }


    @Test
    public void testSerializeCSTR() throws Exception {

        ByteBuffer buff = ByteBuffer.allocate(1024);

        assert(BSONObject.serializeCSTR(buff, "name") == 5);

        buff.position(0);

        assert(BSONObject.serializeCSTR(buff, "b") == 2);
    }


    @Test
    public void testDeSerializeCSTR() throws Exception {

        ByteBuffer buff = ByteBuffer.allocate(1024);

        assert(BSONObject.serializeCSTR(buff, "name") == 5);

        buff.position(0);

        assert(BSONObject.deserializeCSTR(buff).equals("name"));
    }

    @Test
    public void testDate() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        Date now = new Date();

        md.put("date", now);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        assert(md2.get("date").equals(now));

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("date").equals(now));
    }

    @Test
    public void testBoolean() throws Exception {

        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        md.put("date", true);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

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

        Doc md = new Doc();

        md.put("date", null);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        assert(md2.get("date") == null);

        byte[] barr = bo.toArray();

        BSONObject bo2 = new BSONObject();

        md2 = bo2.deserialize(barr);

        assert(md2.get("date") == null);
    }

    @Test
    public void testArray() throws Exception {
        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        String[] arr = new String[] { "a", "b", "c"};

        md.put("array", arr);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        List l = (List) md2.get("array");

        assert(l.size() == arr.length);
        
        for (int i = 0; i < arr.length; i++) {
            assert (l.get(i).equals(arr[i]));
        }
    }


    @Test
    public void testListAsArray() throws Exception {
        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        List<String> l = new ArrayList<String>();

        l.add("x");
        l.add("y");
        l.add("z");

        md.put("list", l);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        List ll= (List) md2.get("list");

        assert(ll.size() == l.size());
        
        for (int i = 0; i < ll.size(); i++) {
            assert (l.get(i).equals(ll.get(i)));
        }
    }

    @Test
    public void testOID() throws Exception {
        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        BabbleOID oid = new BabbleOID();

        md.put("oid", oid);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        BabbleOID oid2 = (BabbleOID) md2.get("oid");

        assert(oid2.toString().equals(oid.toString()));
    }

    @Test
    public void testRef() throws Exception {

        Doc md = new Doc();

        BSONRef ref = new BSONRef("mystring", new BabbleOID("000102030405060708090a0b"));

        md.put("ref", ref);

        BSONObject bo = new BSONObject();

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        BSONRef ref2 = (BSONRef) md2.get("ref");


        assert(ref2.getNamespace().equals(ref.getNamespace()));

        assert(ref2.getOID().toString().equals(ref.getOID().toString()));
    }

    @Test
    public void testBinary() throws Exception {

        Doc md = new Doc();

        byte[] arr = {1, 2, 3, 4};

        md.put("binary", arr);

        BSONObject bo = new BSONObject();

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        byte[] arr2 = (byte[]) md2.get("binary");

        assert(arr2[0] == 1);
        assert(arr2[1] == 2);
        assert(arr2[2] == 3);
        assert(arr2[3] == 4);
    }

    @Test
    public void testRegex() throws Exception {
        BSONObject bo = new BSONObject();

        Doc md = new Doc();

        Pattern p = Pattern.compile("foo*", Pattern.CASE_INSENSITIVE);

        md.put("pattern", p);

        bo.serialize(md);

        Doc md2 = bo.deserialize();

        p = (Pattern) md2.get("pattern");

        assert(p.pattern().equals("foo*"));
        assert(p.flags() == Pattern.CASE_INSENSITIVE);

        p = Pattern.compile("bar*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        md.put("pattern", p);

        bo = new BSONObject();  // huh?
        
        bo.serialize(md);

        md2 = bo.deserialize();

        p = (Pattern) md2.get("pattern");

        assert(p.pattern().equals("bar*"));
        assert(p.flags() == (Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));

    }


}
