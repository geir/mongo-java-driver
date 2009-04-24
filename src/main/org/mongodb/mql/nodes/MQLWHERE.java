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
import org.mongodb.mql.MQLTreeConstants;

import java.util.Set;
import java.util.HashSet;

public class MQLWHERE extends SimpleNode {
    public MQLWHERE(int id) {
        super(id);
    }

    public MQLWHERE(MQL p, int id) {
        super(p, id);
    }

    public void render(QueryInfo qi) {

        Set<String> set = new HashSet<String>();

        qi.setWhereClause(walk((SimpleNode) this.children[0], set));

        for (String s : set) {
            System.out.println(" -> " + s);
        }
    }

    public String walk(SimpleNode sn, Set<String> fields) {

        StringBuilder sb = new StringBuilder();

        int i = sn.jjtGetNumChildren();

        if (i > 0) {

            SimpleNode left = (SimpleNode) sn.jjtGetChild(0);

            if (left.getId() == MQLTreeConstants.JJTPATH) {
                sb.append("obj.");
                sb.append(left.stringJSForm());

                fields.add(left.stringJSForm());
            } else {
                sb.append(walk(left, fields));
            }
        }

        sb.append(sn.stringJSForm());

        if (i > 1) {
            SimpleNode right = (SimpleNode) sn.jjtGetChild(1);

            if (right.getId() == MQLTreeConstants.JJTPATH) {
                sb.append("obj.");
                sb.append(right.stringJSForm());

                fields.add(right.stringJSForm());

            } else {
                sb.append(walk(right, fields));
            }
        }

        return sb.toString();
    }
}
/* JavaCC - OriginalChecksum=53666b8576c10a72d9f2f4df7d4dd0ee (do not edit this line) */
