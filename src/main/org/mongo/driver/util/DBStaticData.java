package org.mongo.driver.util;

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
