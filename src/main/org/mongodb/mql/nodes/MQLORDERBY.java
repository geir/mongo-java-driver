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
package org.mongodb.mql.nodes;

import org.mongodb.mql.SimpleNode;
import org.mongodb.mql.MQL;
import org.mongodb.mql.QueryInfo;
import org.mongodb.mql.Node;
import org.mongodb.mql.MQLTreeConstants;

public class MQLORDERBY extends SimpleNode {
    public MQLORDERBY(int id) {
        super(id);
    }

    public MQLORDERBY(MQL p, int id) {
        super(p, id);
    }

    public void render(QueryInfo qi) {

        for (Node n : children) {

            SimpleNode sn = (SimpleNode) n;

            assert (sn.getId() == MQLTreeConstants.JJTORDERBYITEM);

            String what = ((SimpleNode) sn.jjtGetChild(0)).stringJSForm();

            int id  = MQLTreeConstants.JJTASCENDING;

            if (sn.jjtGetNumChildren() > 1) {
                id = ((SimpleNode) sn.jjtGetChild(1)).getId();
            }
            
            qi.addOrderBy(new QueryInfo.Field(what, ((SimpleNode) sn.jjtGetChild(0)).getId(),
                    id == MQLTreeConstants.JJTASCENDING ? "1" : "-1", MQLTreeConstants.JJTINTEGERLITERAL));
        }
    }
}
/* JavaCC - OriginalChecksum=637cca873460b9bf0c6ad8d924bcf86b (do not edit this line) */
