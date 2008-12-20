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

public class MQLSELECTEXPRESSION extends SimpleNode {
    public MQLSELECTEXPRESSION(int id) {
        super(id);
    }

    public MQLSELECTEXPRESSION(MQL p, int id) {
        super(p, id);
    }

    public void render(QueryInfo qi) {

        SimpleNode sn = (SimpleNode) children[0];

        if (sn.getId() == MQLTreeConstants.JJTAGGREGATE) {
            // a count(*) so just do the right thing...

            SimpleNode agg = (SimpleNode) sn.jjtGetChild(0);
            agg.updateQI(qi);
        } else {
            // assume a field selection expression

            qi.addReturnField(new QueryInfo.Field(sn.stringJSForm(), MQLTreeConstants.JJTPATH));
        }
    }
}
/* JavaCC - OriginalChecksum=6df2c59ee984913560de61cab3f8ee76 (do not edit this line) */
