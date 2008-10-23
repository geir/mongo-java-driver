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

package org.mongodb.driver.util;

/**
 * User: geir
 * Date: Oct 13, 2008
 * Time: 5:25:26 AM
 */
public class DBStaticData {

    /**
     *  Database operations
     */
    public static final int OP_REPLY = 1;     /* reply. responseTo is set. */
    public static final int OP_MSG = 1000;    /* generic msg command followed by a string */
    public static final int OP_UPDATE = 2001;    /* update object */
    public static final int OP_INSERT = 2002;
    // public static final int GET_BY_OID = 2003;
    public static final int OP_QUERY = 2004;
    public static final int OP_GET_MORE = 2005;
    public static final int OP_DELETE = 2006;
    public static final int OP_KILL_CURSORS = 2007;


}
