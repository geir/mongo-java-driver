package org.mongo.driver.impl.msg;

import org.mongo.driver.impl.msg.DBMessage;
import org.mongo.driver.util.DBStaticData;
import org.mongo.driver.MongoDBException;

/**
 * User: geir
 * Date: Oct 13, 2008
 * Time: 6:03:12 AM
 */
public class DBMsgMessage  extends DBMessage {

    protected String _msg;

    public DBMsgMessage(String msg) throws MongoDBException {
        super(DBStaticData.OP_MSG);
        _msg = msg;

        this.writeString(_msg);
    }
}
