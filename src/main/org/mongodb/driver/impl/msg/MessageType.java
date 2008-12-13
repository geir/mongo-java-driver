package org.mongodb.driver.impl.msg;

import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;

/**
 *  MongoDB wire protocol message types
 */
public enum MessageType
{
    OP_REPLY(1),          // reply. responseTo is set
    OP_MSG(1000),         // generic msg command followed by a string
    OP_UPDATE(2001),      // update object
    OP_INSERT(2002),      // insert new object
    OP_GET_BY_OID(2003),  // is this used?
    OP_QUERY(2004),
    OP_GET_MORE(2005),
    OP_DELETE(2006),
    OP_KILL_CURSORS(2007);

    private static final Map<Integer,MessageType> _reverseMap  = new HashMap<Integer,MessageType>();

    static {
        for(MessageType type : EnumSet.allOf(MessageType.class)) {
            _reverseMap.put(type.getOpCode(), type);
        }
    }

    private final int _opcode;

    private MessageType(int opcode) {
        _opcode = opcode;
    }

    public int getOpCode() {
        return _opcode;
    }

    public static MessageType get(int opcode) {
        return _reverseMap.get(opcode);
    }
}
