package org.mongo.driver.util;

import org.mongo.driver.MongoDoc;
import org.mongo.driver.DBObjectID;
import org.mongo.driver.MongoDBException;

import java.util.Date;
import java.util.Map;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;

/**
 * Utility representation of a BSON Document, the binary serialization of a
 * JSON document
 * 
 */
public class BSONObject {

    static final byte EOO = 0;      // x
    static final byte MAXKEY = -1;
    static final byte NUMBER = 1;   // x
    static final byte STRING = 2;   // x
    static final byte OBJECT = 3;
    static final byte ARRAY = 4;
    static final byte BINARY = 5;
    static final byte UNDEFINED = 6;
    static final byte OID = 7;       // x
    static final byte BOOLEAN = 8;
    static final byte DATE = 9;
    static final byte NULL = 10;
    static final byte REGEX = 11;
    static final byte REF = 12;
    static final byte CODE = 13;
    static final byte SYMBOL = 14;
    static final byte CODE_W_SCOPE = 15;

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

        int messageSize = 0;

        // put in a placeholder for the total size
        _buf.putInt(0);
        messageSize += 4;  //

        // now put in the doc elements

        for (String key : m.orderedKeyList()) {
            Object v = m.get(key);

            switch(getType(v)) {
                
                case STRING :
                    messageSize += serializeStringElement(_buf, key, (String) v);
                    break;

                case NUMBER :
                    messageSize += serializeNumberElement(_buf, key, (Number) v);
                    break;

                case OBJECT :
                    messageSize += serializeObjectElement(_buf, key, (MongoDoc) v);
                    break;

                case OID :
                    messageSize += serializeOIDElement(_buf, key, (DBObjectID) v);
                    break;

                case BOOLEAN :
                    messageSize += serializeBooleanElement(_buf, key, (Boolean) v);
                    break;

                default :
                    throw new MongoDBException("Unknown type " + getType(v));
            }
        }

        // add the EOO

        messageSize += serializeEOOElement(_buf);

        _buf.putInt(0, messageSize);
        _buf.flip();
    }

    /**
     *  Deserializes a BSON document into a MongoDoc object
     *
     * @param byteBuff buffer of BSON to deserialize
     * @return new mongo doc
     * @throws Exception if a problem
     */
    public MongoDoc deserialize(byte[] byteBuff) throws MongoDBException {
        _buf = ByteBuffer.wrap(byteBuff);
        _buf.order(ByteOrder.LITTLE_ENDIAN);

        /*
         *  is this necessary?
         */
        int messageSize = _buf.getInt();
        _buf.limit(messageSize);
        assert(messageSize == byteBuff.length);
        _buf.position(0);

        return deserialize();
    }

    public MongoDoc deserialize() throws MongoDBException {

        _buf.position(0);
        
        /*
         *  eat the message size
         */
        _buf.getInt();

        MongoDoc doc = new MongoDoc();

        /*
         * now process the elements elements :   <element> -> <element_type> <element_name> <element_data>
         */
        while (_buf.hasRemaining()) {

            byte type = _buf.get();

            String key;

            switch (type) {
                case STRING :
                    key = deserializeElementName(_buf);
                    doc.put(key, deserializeSTRINGData(_buf));
                    break;

                case NUMBER :
                    key = deserializeElementName(_buf);
                    doc.put(key, deserializeNUMBERData(_buf));
                    break;

                case OID :
                    key = deserializeElementName(_buf);
                    doc.put(key, deserializeOIDData(_buf));
                    break;

                case OBJECT :
                    key = deserializeElementName(_buf);
                    doc.put(key, deserializeObjectData(_buf));
                    break;

                case BOOLEAN :
                    key = deserializeElementName(_buf);
                    doc.put(key, deserializeBooleanData(_buf));
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
    protected Double deserializeNUMBERData(ByteBuffer buf) {
        return buf.getDouble();
    }

    /**
     *  Deserializes the data for a STRING element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     */
    protected MongoDoc deserializeObjectData(ByteBuffer buf) throws MongoDBException {

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

        return o.deserialize(arr);
    }

    /**
     *  Deserializes the data for a STRING element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws Exception if an encoding problem
     */
    protected String deserializeSTRINGData(ByteBuffer buf) throws MongoDBException {

        int len = buf.getInt();  // the buffers size includes the null terminator

        buf.get(_privateBuff, 0, len);

        try {
            return new String(_privateBuff, 0, len-1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MongoDBException("Encoding exception", e);
        }
    }

    /**
     *  Deserializes the data for a OID element type.
     *
     * @param buf buffer in which next sequence of bytes is an STRING element
     * @return deserialized String
     * @throws Exception if an encoding problem
     */
    protected DBObjectID deserializeOIDData(ByteBuffer buf) throws MongoDBException {

        buf.get(_privateBuff, 0, 12);

        return new DBObjectID(_privateBuff);
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
     * @param val val
     * @return number of bytes used in buffer
     * @throws Exception on error
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
     * @throws Exception on error
     */
    protected int serializeNumberElement(ByteBuffer buf, String key, Number val) throws MongoDBException {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(NUMBER);
        bufSizeDelta++;

        /*
         * set the key string
         */
        bufSizeDelta += serializeCSTR(buf, key);

        /*
         * set the value :  just serialize it
         */

        buf.putDouble(val.doubleValue());

        bufSizeDelta += 8;

        return bufSizeDelta;
    }

    /**
     *   <data_object> -> <bson_object>
     *     *
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @return number of bytes used in buffer
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
     */
    protected int serializeOIDElement(ByteBuffer buf, String key, DBObjectID val) throws MongoDBException {

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

    /**
     *   <data_string> -> (int32) length <cstring> where
     *
     *   <cstring> -> UTF-8-encoded characters ended by 0 (byte?)
     * 
     * @param buf buffer to write into
     * @param key key
     * @param val val
     * @return number of bytes used in buffer
     */
    protected int serializeStringElement(ByteBuffer buf, String key, String val) {

        /*
         * set the type byte
         */
        int bufSizeDelta = 0;
        buf.put(STRING);
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

    /**
     *   Reads the element name.  Unlike a STRING, this isn't prefixed with a size so we have to do
     *   it byte by byte
     *
     * @param buf buffer to read from
     * @return element name
     * @throws Exception if an encoding problem
     */
    protected String deserializeElementName(ByteBuffer buf) throws MongoDBException {

        int i = 0;

        while(true) {
            byte b = buf.get();

            /*
             *  cstrings are terminated by a 0 byte
             */
            if (b == 0) {
                break;
            }

            this._privateBuff[i++] = b;
         }

        try {
            return new String(_privateBuff, 0, i, "UTF-8");
        } catch (UnsupportedEncodingException e) {
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

    public static byte getType(Object o) throws MongoDBException {
        if ( o == null )
            return NULL;

        if ( o instanceof Number )
            return NUMBER;

        if ( o instanceof String)
            return STRING;

        if ( o.getClass().isArray())
            return ARRAY;

//        if ( o instanceof JSBinaryData )
//            return BINARY;

        if ( o instanceof DBObjectID)
            return OID;

        if ( o instanceof Boolean )
            return BOOLEAN;

        if ( o instanceof Date)
            return DATE;

//        if ( o instanceof JSRegex )
//            return REGEX;

        if ( o instanceof MongoDoc )
            return OBJECT;

        throw new MongoDBException("Unknown type of object : " + o.getClass());
    }
}
