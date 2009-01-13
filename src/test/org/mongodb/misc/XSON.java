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

package org.mongodb.misc;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.mongodb.driver.util.types.BabbleOID;
import org.mongodb.driver.util.types.BSONRegex;
import org.mongodb.driver.ts.MongoSelector;
import org.mongodb.driver.ts.MongoDoc;
import org.mongodb.driver.MongoDBException;
import org.mongodb.driver.util.BSONObject;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;


/**
 *  XSON (XML Serialized 'ocument Notation) - XML
 *
 */
public class XSON extends DefaultHandler {

    Map<String, Class> _handlerMap = new HashMap<String, Class>() {
        {
            put("twonk", TwonkHandler.class);
            put("string", StringHandler.class);
            put("boolean", BooleanHandler.class);
            put("number", NumberHandler.class);
            put("date", DateHandler.class);
            put("code", CodeHandler.class);
            put("doc", DocHandler.class);
            put("binary", BinaryHandler.class);
            put("oid", OIDHandler.class);
            put("array", ArrayHandler.class);
            put("int", IntHandler.class);
            put("regex", RegexHandler.class);
            put("null", StringHandler.class);
        }
    };

    Stack<Handler> _handlerStack = new Stack<Handler>();
    MongoSelector _doc = new MongoSelector();
    MongoSelector _currentDoc = _doc;
    Handler _currentHandler = null;

    public static void main (String args[]) throws Exception
    {
        if (args.length != 3) {
            System.out.println("usage : ");
            System.out.println("  to convert xson to bson : --xtob xson_input_file bson_output_file");
            System.out.println("  to convert bson to xson : --btox bson_input_file xson_output_file");
            return;
        }

        if (args[0].equals("--xtob")) {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            XSON xson = new XSON();

            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( new File(args[1]), xson);

            FileOutputStream fos = new FileOutputStream(new File(args[2]));

            BSONObject bson = new BSONObject();

            bson.serialize((MongoDoc) xson._doc.get("$root"));

            fos.write(bson.toArray());
            fos.close();
        }
        else if (args[0].equals("--btox")) {
            System.out.println("Unimplemented");
        }
    }

    /*
     *  ------------   SAX  stuff    -------------------------
     */

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (_currentHandler == null) {
            _currentHandler = getHandler(qName);
        }

        _currentHandler.startElement(uri, localName, qName, attributes);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        _currentHandler.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        _currentHandler.characters(ch, start, length);
    }

    /*
     *    -------------  handler stuff ----------------------------
     */

    public Handler getHandler(String t) {

        Class c = _handlerMap.get(t);

        if (c == null) {
            System.err.println("WARNING : no handler for " + t);
            return new Handler();
        }

        try {
            return (Handler) c.getConstructors()[0].newInstance(this);
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    public class Handler extends DefaultHandler {

        String _name = null;
        String _value = null;

        public String cleanName() {
            if (_name == null) {
                return "";
            }

            return _name;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            _value = new String(ch, start, length);
        }

        public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
            _name = att.getValue("name");
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (_handlerStack.size() > 0) {
                _currentHandler = _handlerStack.pop();
            }
        }
    }

    public class OIDHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {

            try {
                _currentDoc.put(cleanName(),  new BabbleOID(_value.toUpperCase()));
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class IntHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                _currentDoc.put(cleanName(), Integer.parseInt(_value));
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class BooleanHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                _currentDoc.put(cleanName(), Boolean.parseBoolean(_value));
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class DateHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                _currentDoc.put(cleanName(), new Date(Long.parseLong(_value)));
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class BinaryHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                sun.misc.BASE64Decoder decoder =  new sun.misc.BASE64Decoder();

                if (_value == null) {
                    _value = "";
                }
                _currentDoc.put(cleanName(), decoder.decodeBuffer(_value));
            } catch (MongoDBException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }


    public class CodeHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                _currentDoc.put(cleanName(), _value);
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class NumberHandler extends Handler {
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                _currentDoc.put(cleanName(), Double.parseDouble(_value));
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class RegexHandler extends Handler {

        String _next = null;
        Map<String, String> _data = new HashMap<String,String>();

        public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {

            if ("regex".equals(qName)) {
                super.startElement(uri, localName, qName, att);
                return;
            }

            _next = qName;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            _data.put(_next, new String(ch, start, length));
        }


        public void endElement(String uri, String localName, String qName) throws SAXException {

            if ("regex".equals(qName)) {

                BSONRegex br = new BSONRegex(_data.get("pattern"), _data.get("options"));
//                Pattern p = Pattern.compile(_data.get("pattern"));   // TODO - options
                try {
                    _currentDoc.put(cleanName(), br);
                } catch (MongoDBException e) {
                    e.printStackTrace();
                }
                super.endElement(uri, localName, qName);
            }
            else {
                _next = null;
            }
        }
    }

    public class ArrayHandler extends Handler {

        MongoSelector _oldDoc = null;
        MongoSelector _myDoc = new MongoSelector();

        public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {

            if ("array".equals(qName)) {
                _oldDoc = _currentDoc;
                _currentDoc = _myDoc;
                super.startElement(uri, localName, qName, att);
                return;
            }

            _handlerStack.push(this);
            _currentHandler = getHandler(qName);
            _currentHandler.startElement(uri, localName, qName, att);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            _currentDoc = _oldDoc;
            try {
                List<Object> l = new ArrayList<Object>();

                for (String s : _myDoc.orderedKeyList()) {
                    l.add(_myDoc.get(s));
                }
                _currentDoc.put(cleanName(), l);
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class StringHandler extends Handler {

        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                _currentDoc.put(cleanName(), _value);
            } catch (MongoDBException e) {
                e.printStackTrace();
            }

            super.endElement(uri, localName, qName);
        }
    }

    public class DocHandler extends Handler {

        MongoSelector _oldDoc = null;
        MongoSelector _myDoc = new MongoSelector();

        public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {

            if ("doc".equals(qName)) {
                _oldDoc = _currentDoc;
                _currentDoc = _myDoc;
                super.startElement(uri, localName, qName, att);
                return;
            }

            _handlerStack.push(this);
            _currentHandler = getHandler(qName);
            _currentHandler.startElement(uri, localName, qName, att);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            _currentDoc = _oldDoc;
            try {
                _currentDoc.put(_name == null ? "$root" : _name, _myDoc);
            } catch (MongoDBException e) {
                e.printStackTrace();
            }
            super.endElement(uri, localName, qName);
        }
    }

    public class XSONHandler extends Handler {

        public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
            super.startElement(uri, localName, qName, att);

            if ("xson".equals(qName)) {
                return;
            }

            _handlerStack.push(this);
            _currentHandler = getHandler(qName);
            _currentHandler.startElement(uri, localName, qName, att);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (_handlerStack.size() > 0) {
                System.out.println(" - POP DOC - ");
                _currentHandler = _handlerStack.pop();
            }
        }
    }

    public class TwonkHandler extends Handler {

        public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
            super.startElement(uri, localName, qName, att);
            if ("doc".equals(qName)) {
                _handlerStack.push(this);
                _currentHandler = getHandler(qName);
                _currentHandler.startElement(uri, localName, qName, att);
            }
        }
    }
}
