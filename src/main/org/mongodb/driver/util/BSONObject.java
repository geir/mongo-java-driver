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

package org.mongodb.driver.util;

import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.util.types.BabbleOID;
import org.mongodb.driver.util.types.BSONRegex;
import org.mongodb.driver.util.types.BSONRef;
import org.mongodb.driver.util.types.BSONSymbol;
import org.mongodb.driver.util.types.BSONUndefined;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.MongoDBException;

import java.lang.StringBuilder;
import java.util.Formatter;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
 * Utility representation of a BSON Document, the binary serialization of a
 * JSON document
 *
 */
public class BSONObject {

    static final byte EOO = 0;      // x
    static final byte MAXKEY = -1;  // x
    static final byte NUMBER = 1;   // x t
    static final byte STRING = 2;   // x t
    static final byte OBJECT = 3;   // x t
    static final byte ARRAY = 4;    // x t
    static final byte BINARY = 5;   // x t
    static final byte UNDEFINED = 6;// s
    static final byte OID = 7;      // x t
    static final byte BOOLEAN = 8;  // x t
    static final byte DATE = 9;     // x t
    static final byte NULL = 10;    // x t
    static final byte REGEX = 11;   // x t
    static final byte REF = 12;     // x t
    static final byte CODE = 13;    // x t
    static final byte SYMBOL = 14;
    static final byte CODE_W_SCOPE = 15;
    static final byte NUMBER_INT = 16;


    // private types

    private static final int _DEFAULT_BYTEBUF_SIZE = 1024 * 10;

    private byte[] _privateBuff = new byte[1024];

    private ByteBuffer _buf;

    public BSONObject() {
        this(_DEFAULT_BYTEBUF_SIZE);
    }

    public BSONObject(int bufSize) {
        _buf  = ByteBuffer.allocate(bufSize);
        _buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     *  Extracts a copy of the document as a byte array
     * @return array of bytes containing the BSON document
     */
    public byte[] toArray() {

        byte[] msg = new byte[_buf.limit()];
        _buf.get(msg);

        return msg;
    }

    public void serialize(MongoDoc m) throws MongoDBException {

        if (m == null) {
            throw new MongoDBException("Document was null");
        }

        _buf.position(0);

        int messageSize = 0;

        // put in a placeholder for the total size
        _buf.putInt(0);
        messageSize += 4;  //

        // now put in the doc elements

        for (String key : m.orderedKeyList()) {
            Object v = m.get(key);

            switch(getType(v, key)) {

                case STRING :
                    messageSize += serializeStringElement(_buf, key, (String) v, STRING);
                    break;

                case SYMBOL :
                    messageSize += serializeStringElement(_buf, key, ((BSONSymbol) v).getSymbol(), SYMBOL);
                    break;

                case CODE :
                    messageSize += serializeStringElement(_buf, key, (String) v, CODE);
                    break;

                case NUMBER :
                    messageSize += serializeNumberElement(_buf, key, (Number) v, NUMBER);
                    break;

                case NUMBER_INT :
                    messageSize += serializeNumberElement(_buf, key, (Number) v, NUMBER_INT);
                    break;

                case OBJECT :
                    messageSize += serializeObjectElement(_buf, key, (MongoDoc) v);
                    break;

                case OID :
                    messageSize += serializeOIDElement(_buf, key, (BabbleOID) v);
                    break;

                case BOOLEAN :
                    messageSize += serializeBooleanElement(_buf, key, (Boolean) v);
                    break;

                case DATE :
                    messageSize += serializeDateElement(_buf, key, (Date) v);
                    break;

                case NULL :
                case UNDEFINED :
                    messageSize += serializeNullElement(_buf, key, getType(v, key));
                    break;

                case ARRAY:
                    messageSize += serializeArrayElement(_buf, key, v);
                    break;

                case REGEX:
                    messageSize += serializeRegexElement(_buf, key, v);
                    break;

                case BINARY:
                    messageSize += serializeBinaryElement(_buf, key, v);
                    break;

                case REF:
                    messageSize += serializeRefElement(_buf, key, (BSONRef) v);
                    break;

                default :
                    throw new MongoDBException("Unhandled type " + getType(v, key));
            }
        }

        // add the EOO

        messageSize += serializeEOOElement(_buf);

        _buf.putInt(0, messageSize);
        _buf.flip();
    }


    public MongoDoc deserialize(byte[] byteBuff) throws MongoDBException {

        return deserialize(byteBuff, true);
    }

    /**
     *  Deserializes a BSON document into a MongoDoc object
     *
     * @param byteBuff buffer of BSON to deserialize
     * @param keySafety true if you want keys checked, false if not (for de-serializing MongoSelectors and MongoModifiers)
     * @return new mongo doc
     * @throws MongoDBException if a problem
     */
    public MongoDoc deserialize(byte[] byteBuff, boolean keySafety) throws MongoDBException {
        _buf = ByteBuffer.wrap(byteBuff);
        _buf.order(ByteOrder.LITTLE_ENDIAN);

        /*
         *  is this necessary?
         */
        int messageSize = _buf.getInt();
        _buf.limit(messageSize);
        assert(messageSize <= byteBuff.length);  // comeone could pass a buffer bigger than the message
        _buf.position(0);

        MongoDoc md = keySafety ? new MongoDoc() : new MongoDoc() {
                protected void checkKey(String key) throws MongoDBException {
                }
            };
        _deserializeInto(md, keySafety);
        return md;
    }

    public MongoDoc deserialize() throws MongoDBException {

        MongoDoc md = new MongoDoc();

        return _deserializeInto(md, true);
    }

    private MongoDoc _deserializeInto(MongoDoc doc, boolean keySafety) throws MongoDBException {

        _buf.position(0);

        /*
         *  eat the message size
         */
        int totalSize = _buf.getInt();

        /*
         * now process the elements elements :   <element> -> <element_type> <element_name> <element_data>
         */
        while (_buf.hasRemaining()) {

            byte type = _buf.get();

            String key;

            switch (type) {
                case STRING:
                case CODE:
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeSTRINGData(_buf));
                    break;

                case SYMBOL:
                    key = deserializeCSTR(_buf);
                    doc.put(key, new BSONSymbol(deserializeSTRINGData(_buf)));
                    break;

                case NUMBER :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeNumberData(_buf));
                    break;

                case NUMBER_INT :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeNumberIntData(_buf));
                    break;

                case OID :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeOIDData(_buf));
                    break;

                case OBJECT :
                    key = deserializeCSTR(_buf);
                    doc.put(key, _deserializeObjectData(_buf, keySafety));
                    break;

                case BOOLEAN :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeBooleanData(_buf));
                    break;

                case DATE :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeDateData(_buf));
                    break;

                case NULL :
                case UNDEFINED :
                    key = deserializeCSTR(_buf);
                    doc.put(key, (String) null);
                    break;

                case ARRAY :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeArrayData(_buf));
                    break;

                case REGEX :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeRegexData(_buf, totalSize));
                    break;

                case BINARY :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeBinary(_buf));
                    break;

                case REF :
                    key = deserializeCSTR(_buf);
                    doc.put(key, deserializeRef(_buf, totalSize));
                    break;

                case EOO:
                    break;

                default :
                    throw new MongoDBException("Unknown type " + type);
            }
        }

        _buf.flip();

        return doc;
    }

    /**
     * Formats the BSON data to be suitable for a hex dump.
     *
     * @return String containing a hex dump representation of this BSONObject
     */
    public String getHexDump () {
        byte[] bytes = this.toArray();
        StringBuilder to_return = new StringBuilder();
        Formatter formatter = new Formatter(to_return);

        for (int i = 0; i < bytes.length; i++) {
            if (i % 8 == 0) {
                if (i != 0) {
                    to_return.append("\n");
                }
                formatter.format("%4d:  ", i);
            } else {
                to_return.append(" ");
            }
            formatter.format("%02X", bytes[i]);
        }
        return to_return.toString();
    }

    /**
     *  Deserializes the data for a Date element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     */
    protected Date deserializeDateData(ByteBuffer buf) {

        return new Date(buf.getLong());
    }

    /**
     *  Deserializes the data for a Boolean element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     */
    protected Boolean deserializeBooleanData(ByteBuffer buf) {

        return (buf.get() == (byte) 1);
    }

    /**
     *  Deserializes the data for a NUMBER element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     */
    protected Double deserializeNumberData(ByteBuffer buf) {
        return buf.getDouble();
    }

    /**
     *  Deserializes the data for a NUMBER_INT element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     */
    protected Integer deserializeNumberIntData(ByteBuffer buf) {
        return buf.getInt();
    }


    private static byte[] deserializeBinary(ByteBuffer buf) throws MongoDBException{

        int len = buf.getInt();

        byte[] woogie = new byte[len];

        buf.get(woogie);

        return woogie;
    }

    private BSONRef deserializeRef(ByteBuffer buf, int totalSize) throws MongoDBException {

               
        String ns = this.deserializeSTRINGData(buf);

        /*
         *  now read the 12 byte OID
         */

        BabbleOID oid = deserializeOIDData(buf);

        return new BSONRef(ns, oid);        
    }

    /**
     *  Deserializes the data for a OBJECT element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws MongoDBException on error
     */
    public static MongoDoc deserializeObjectData(ByteBuffer buf) throws MongoDBException {

        return _deserializeObjectData(buf, true);
    }

    private static MongoDoc _deserializeObjectData(ByteBuffer buf, boolean keySafety) throws MongoDBException{

        /*
         * read the first 4 bytes (size) into a ByteBuf, and get that
         */
        ByteBuffer tempBuf  = ByteBuffer.allocate(4);
        tempBuf.order(ByteOrder.LITTLE_ENDIAN);

        buf.get(tempBuf.array());

        int size = tempBuf.getInt();

        tempBuf.position(0);

        /**
         *   get the remaining bytes of the mongodoc
         */
        byte[] arr = new byte[size];

        buf.get(arr, 4, size-4);

        System.arraycopy(tempBuf.array(), 0, arr, 0, 4);

        BSONObject o = new BSONObject();

        return o.deserialize(arr, keySafety);
    }

    /**
     *  Gets an OBJECT from a socket channel using the specified ByteBuffer
     *
     * @param sc socket channel to read from
     * @param buf buffer to use
     * @return new MongoSelector object
     * @throws MongoDBException in facse something goes wrong
     */
    public static MongoSelector deserializeSelector(SocketChannel sc, ByteBuffer buf) throws MongoDBException {

        try {
            buf.clear();

            int toRead = 4;
            buf.limit(toRead);

            int read = 0;

            while(read < toRead) {
                read += sc.read(buf);
            }

            buf.flip();

            toRead = buf.getInt();

            buf.limit(toRead);

            while (read < toRead ) {
                read += sc.read(buf);
            }

            buf.flip();
            
            return deserializeSelector(buf);
        }
        catch(IOException ioe) {
            throw new MongoDBException("Error : error reading", ioe);
        }
    }

    /**
     *  Deserializes the data for a OBJECT element type that is known to
     *  be a selector.  This has the effect of relazing the key constraints on the
     *  object (so that magic values like "$foo" are allowed)
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws MongoDBException on error
     */
    public static MongoSelector deserializeSelector(ByteBuffer buf) throws MongoDBException {

        MongoDoc md = _deserializeObjectData(buf, false);

        return new MongoSelector(md.getMap());  // TODO - clean this mess up
    }

    /**
     *  Deserializes the data for a OBJECT element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws MongoDBException on error
     */
    protected List deserializeArrayData(ByteBuffer buf) throws MongoDBException {

        MongoDoc doc = deserializeObjectData(buf);

        Object[] arr = new Object[doc.size()];

        for (Object o : doc.getMap().entrySet()) {

            Map.Entry e = (Map.Entry) o;

            int loc = Integer.valueOf(e.getKey().toString());

            if (loc > doc.size() - 1) {
                throw new MongoDBException ("ERROR - key value out of range of index for array" + loc);
            }

            arr[loc] = e.getValue();
        }

        List l = new ArrayList();

        for (Object o : arr) {
            l.add(o);
        }
        return l;
    }


    /**
     *  Deserializes the data for a STRING element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws MongoDBException if an encoding problem
     */
    protected String deserializeSTRINGData(ByteBuffer buf) throws MongoDBException {

        int len = buf.getInt();  // the buffers size includes the null terminator

        byte[] bytes = new byte[len];

        buf.get(bytes, 0, len);

        try {
            return new String(bytes, 0, len-1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MongoDBException("Encoding exception", e);
        }
    }

    /**
     *  Deserializes the data for a OID element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws MongoDBException if an encoding problem
     */
    protected BabbleOID deserializeOIDData(ByteBuffer buf) throws MongoDBException {

        byte[] bytes = new byte[12];

        buf.get(bytes, 0, 12);

        return new BabbleOID(bytes);
    }

    /**
     *  Deserializes the data for a REGEX element type.
     *
     * @param buf buffer in which next sequence of bytes is an REGEX element
     * @param totalSize - total size of message just to have a safety
     * @return deserialized String
     * @throws MongoDBException if an encoding problem
     */
    protected Pattern deserializeRegexData(ByteBuffer buf, int totalSize) throws MongoDBException {

        /*
         * there is no sizing info - need to just search for nulls
         */

        try {

            String pattern = "";
            String flagString;
            int i = 0;
            int loc = 0;

            /*
             *   first go find the null for the pattern and save that string
             */
            for (; i < totalSize; i++, loc++) {
                _privateBuff[loc] = buf.get();

                if (_privateBuff[loc] == 0) {
                    pattern =  new String(_privateBuff, 0, loc, "UTF-8");
                    break;
                }
            }

            if ( i == totalSize) {
                throw new MongoDBException("ERROR - can't find null boundaries in a regex object");
            }

            /*
             *  next go find the null for the options and save *that* string
             */
            i++;

            for (loc = 0; i < totalSize; i++, loc++) {
                _privateBuff[loc] = buf.get();

                if (_privateBuff[loc] == 0) {
                    flagString =  new String(_privateBuff, 0, loc, "UTF-8");

                    int flags = 0;

                    if (flagString.contains("i")) {
                        flags |= Pattern.CASE_INSENSITIVE;
                    }

                    if (flagString.contains("m")) {
                        flags |= Pattern.MULTILINE;
                    }

                    return Pattern.compile(pattern, flags);
                }
            }

            throw new MongoDBException("ERROR - can't find null boundaries in a regex object");
            
        } catch (UnsupportedEncodingException e) {
            throw new MongoDBException("Encoding exception", e);
        }
    }


    /**
     *
     * @param buf buffer to write into
     * @return number of bytes used in buffer
     */
    protected int serializeEOOElement(ByteBuffer buf) {

        /*
         * set the type byte
         */
        buf.put(EOO);

        return 1;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @return number of bytes used in buffer
     * @throws MongoDBException on error
     */
    protected int serializeNullElement(ByteBuffer buf, String key, byte opcode) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(opcode);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        return bufSizeDelta;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @return number of bytes used in buffer
     * @throws MongoDBException on error
     */
    protected int serializeBooleanElement(ByteBuffer buf, String key, Boolean val) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(BOOLEAN);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  just serialize it
         */

        buf.put( val? (byte) 1 : (byte) 0);
        bufSizeDelta += 1;

        return bufSizeDelta;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @return number of bytes used in buffer
     * @throws MongoDBException on error
     */
    protected int serializeDateElement(ByteBuffer buf, String key, Date val) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(DATE);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  serialize the internal long
         */

        buf.putLong(val.getTime());
        bufSizeDelta += 8;

        return bufSizeDelta;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @param type either NUMBER or NUMBER_INT
     * @return number of bytes used in buffer
     * @throws MongoDBException on error
     */
    protected int serializeNumberElement(ByteBuffer buf, String key, Number val, byte type) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(type);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  just serialize it
         */

        if (type == NUMBER) {
            buf.putDouble(val.doubleValue());
            bufSizeDelta += 8;
        }
        else if (type == NUMBER_INT) {
            buf.putInt(val.intValue());
            bufSizeDelta += 4;
        }

        return bufSizeDelta;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @return number of bytes used in buffer
     * @throws MongoDBException on error
     */
    protected int serializeObjectElement(ByteBuffer buf, String key, MongoDoc val) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(OBJECT);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  ljust serialize it
         */

        BSONObject o = new BSONObject();

        o.serialize(val);

        byte[] arr = o.toArray();

        buf.put(arr);

        bufSizeDelta += arr.length;

        return bufSizeDelta;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @return number of bytes used in buffer
     * @throws MongoDBException on error
     */
    protected int serializeOIDElement(ByteBuffer buf, String key, BabbleOID val) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(OID);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        byte[] arr = val.getArray();

        buf.put(arr);
        bufSizeDelta += 12;

        return bufSizeDelta;
    }

    protected int serializeRefElement(ByteBuffer buf, String key, BSONRef val) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(REF);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  length cstr - first set a hole for the string size
         */
        int pos = buf.position();
        buf.putInt(0);
        bufSizeDelta += 4;


        /*
         * now the ns
         */
        int strSize = serializeCSTR(buf, val.getNamespace());
        bufSizeDelta += strSize;

        /*
         *  now that we know the size, patch it in the front.
         */
        buf.putInt(pos, strSize);

        /*
         * and then the OID  - TODO refactor w/ real OID code
         */

        byte[] arr = val.getOID().getArray();

        buf.put(arr);
        bufSizeDelta += 12;

        return bufSizeDelta;
    }

    /**
     *   <data_string> -> (int32) length <cstring> where
     *
     *   <cstring> -> UTF-8-encoded characters ended by 0 (byte?)
     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @param type either STRING or CODE (they are the same...)
     * @return number of bytes used in buffer
     */
    protected int serializeStringElement(ByteBuffer buf, String key, String val, byte type) {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(type);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  length cstr - first set a hole for the string size
         */
        int pos = buf.position();
        buf.putInt(0);
        bufSizeDelta += 4;


        /*
         * set the value - a CSTR (UTF-8 encoded bytes w/ a 0 byte terminator)
         */
        int strSize = serializeCSTR(buf, val);
        bufSizeDelta += strSize;

        /*
         *  now that we know the size, patch it in the front.
         */
        buf.putInt(pos, strSize);

        return bufSizeDelta;
    }

    protected int serializeArrayElement(ByteBuffer buf, String key, Object v) throws MongoDBException {
        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(ARRAY);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         *   now, what is this thing?
         */

        List l;

        /*
         *   I guess we should be nice and take Lists as well
         */

        if (v.getClass().isArray()) {
            l = Arrays.asList((Object[]) v);
        }
        else if (v instanceof List) {
            l = (List) v;
        }
        else {
            throw new MongoDBException("I don't know how to handle " + v.getClass());
        }

        MongoDoc m = new MongoDoc();

        int i = 0;
        for (Object o : l) {
            m.put(String.valueOf(i++), o);
        }

        BSONObject o = new BSONObject();

        o.serialize(m);

        byte[] arr = o.toArray();

        buf.put(arr);

        bufSizeDelta += arr.length;

        return bufSizeDelta;
    }

    protected int serializeBinaryElement(ByteBuffer buf, String key, Object v) throws MongoDBException {

        if (!(v instanceof byte[])) {
            throw new MongoDBException("Error : serializeBinaryElement : don't know how to handle " + v.getClass());
        }

        byte[] arr = (byte[]) v;

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(BINARY);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the size
         */

        buf.putInt(arr.length);
        bufSizeDelta += 4;

        /*
         * just write the bytes out
         */

        buf.put(arr);
        bufSizeDelta += arr.length;

        return bufSizeDelta;
    }
    
    protected int serializeRegexElement(ByteBuffer buf, String key, Object o) throws MongoDBException {

        BSONRegex bsr = null;

        if (o instanceof Pattern) {

            Pattern v = (Pattern) o;

            int flags = v.flags();

            StringBuffer sb = new StringBuffer("g");  // always throw in global since that's what Java's regex does

            if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
                sb.append("i");
            }
            if ((flags & Pattern.MULTILINE) != 0) {
                sb.append("m");
            }

            bsr = new BSONRegex(v.pattern(), sb.toString());
        }
        else if (o instanceof BSONRegex) {
            bsr = (BSONRegex) o;
        }
        else {
            throw new MongoDBException("Error : serializeRegexElement : don't know how to handle type :" + o.getClass());
        }

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(REGEX);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the pattern
         */
        bufSizeDelta += serializeCSTR(buf, bsr.getPattern());

        /*
         * now get the flags and translate into what JS would do  (WWJSD)
         */

        String options = bsr.getOptions();

        TreeMap<Character, Character> sm = new TreeMap<Character, Character>();

        for (int i=0; i < options.length(); i++) {
            sm.put(options.charAt(i), options.charAt(i));
        }

        StringBuffer sb = new StringBuffer();

        for (char c : sm.keySet()) {
            sb.append(c);
        }

        bufSizeDelta += serializeCSTR(buf, sb.toString());
        
        return bufSizeDelta;
    }


    /**
     *   Reads the element name.  Unlike a STRING, this isn't prefixed with a size so we have to do
     *   it byte by byte
     *
     * @param buf buffer to read from
     * @return element name
     * @throws MongoDBException if an encoding problem
     */
    public static String deserializeCSTR(ByteBuffer buf) throws MongoDBException {

        int i = 0;

        StringBuffer sb = new StringBuffer();
        byte[] arr = new byte[256];

        try {

            while(true) {
                byte b = buf.get();

                /*
                 *  cstrings are terminated by a 0 byte
                 */
                if (b == 0) {
                    break;
                }

                arr[i++] = b;
                
                if (i == arr.length) {
                    sb.append(new String(arr, 0, i, "UTF-8"));
                    i = 0;
                }
             }

            sb.append(new String(arr, 0, i, "UTF-8"));

            return sb.toString();
        }
        catch (UnsupportedEncodingException e) {
            throw new MongoDBException("Encoding exception : ", e);
        }
    }

    /**
     *  Serilzies a String into a cstr, which is just going to be
     *  UTF-8 encoded characters terminated by a 0 byte.  There is
     *  no prefixed type or size.
     *
     * @param buf buf to write into
     * @param val string to write
     * @return number of bytes written to ByteBuffer
     */
    public static int serializeCSTR(ByteBuffer buf, String val) {

        //  serialize the str into a char buffer, UTF-8 encoding

        int start = buf.position();

        CharBuffer cbuf = CharBuffer.allocate(val.length()*3);
        Charset utf8charset =  Charset.forName( "UTF-8" );  // make this static
        CharsetEncoder encoder = utf8charset.newEncoder();
        cbuf.append(val);
        cbuf.flip();

        encoder.encode(cbuf, buf, false);

        // string terminator
        buf.put((byte) 0);

        return buf.position() - start;
    }

    public static byte getType(Object o, String key) throws MongoDBException {

        if ( o == null ) {
            return NULL;
        }

        if (o instanceof Integer) {
            return NUMBER_INT;
        }

        if ( o instanceof Number ) {
            return NUMBER;
        }

        if ( o instanceof String) {

            // magic awful stuff - the DB requires that a where clause is sent as CODE

            if ("$where".equals(key)) {
                return CODE;
            }
            return STRING;
        }

        // ensure that this is first as a byte[] is not an ARRAY
        
        if ( o instanceof byte[] )
            return BINARY;


        if ( o.getClass().isArray())
            return ARRAY;

        if ( o instanceof List)
            return ARRAY;

        if ( o instanceof BabbleOID)
            return OID;

        if ( o instanceof Boolean )
            return BOOLEAN;

        if ( o instanceof Date)
            return DATE;

        if ( o instanceof Pattern )
            return REGEX;

        if ( o instanceof BSONRegex)
            return REGEX;

        if ( o instanceof MongoDoc )
            return OBJECT;

        if (o instanceof BSONRef) {
            return REF;
        }

        if (o instanceof BSONSymbol) {
            return SYMBOL;
        }

        if (o instanceof BSONUndefined) {
            return UNDEFINED;
        }

        throw new MongoDBException("Unknown type of object : " + o.getClass());
    }
}
