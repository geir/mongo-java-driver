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

package org.mongodb.driver.impl.msg;

import org.mongodb.driver.MongoDBException;

import java.io.InputStream;
import java.io.IOException;

/**
 * Mongo 'message' message
 */
public class DBMsgMessage extends DBMessage {

    protected String _msg;

    public DBMsgMessage(String msg) throws MongoDBException {
        super(MessageType.OP_MSG);
        _msg = msg;

        this.writeString(_msg);
    }
}
