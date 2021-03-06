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
package org.mongodb.mql;

public interface MQLTreeConstants {
    public int JJTVOID = 0;
    public int JJTSELECT = 1;
    public int JJTUPDATE = 2;
    public int JJTDELETE = 3;
    public int JJTFROM = 4;
    public int JJTFROMITEM = 5;
    public int JJTINNERJOIN = 6;
    public int JJTOUTERJOIN = 7;
    public int JJTOUTERFETCHJOIN = 8;
    public int JJTINNERFETCHJOIN = 9;
    public int JJTPATH = 10;
    public int JJTUPDATEITEM = 11;
    public int JJTUPDATEVALUE = 12;
    public int JJTSELECTCLAUSE = 13;
    public int JJTSELECTEXPRESSIONS = 14;
    public int JJTSELECTEXPRESSION = 15;
    public int JJTSELECTEXTENSION = 16;
    public int JJTCONSTRUCTOR = 17;
    public int JJTCLASSNAME = 18;
    public int JJTCONSTRUCTORPARAMS = 19;
    public int JJTCONSTRUCTORPARAM = 20;
    public int JJTAGGREGATE = 21;
    public int JJTDISTINCT = 22;
    public int JJTDISTINCTPATH = 23;
    public int JJTCOUNT = 24;
    public int JJTAVERAGE = 25;
    public int JJTMAX = 26;
    public int JJTMIN = 27;
    public int JJTSUM = 28;
    public int JJTWHERE = 29;
    public int JJTGROUPBY = 30;
    public int JJTGROUPBYEXTENSION = 31;
    public int JJTHAVING = 32;
    public int JJTSUBSELECT = 33;
    public int JJTOR = 34;
    public int JJTAND = 35;
    public int JJTNOT = 36;
    public int JJTBETWEEN = 37;
    public int JJTIN = 38;
    public int JJTLIKE = 39;
    public int JJTISNULL = 40;
    public int JJTISEMPTY = 41;
    public int JJTMEMBEROF = 42;
    public int JJTEXISTS = 43;
    public int JJTANY = 44;
    public int JJTALL = 45;
    public int JJTEQUALS = 46;
    public int JJTNOTEQUALS = 47;
    public int JJTGREATERTHAN = 48;
    public int JJTGREATEROREQUAL = 49;
    public int JJTLESSTHAN = 50;
    public int JJTLESSOREQUAL = 51;
    public int JJTADD = 52;
    public int JJTSUBTRACT = 53;
    public int JJTMULTIPLY = 54;
    public int JJTDIVIDE = 55;
    public int JJTNEGATIVE = 56;
    public int JJTCONCAT = 57;
    public int JJTSUBSTRING = 58;
    public int JJTTRIM = 59;
    public int JJTLOWER = 60;
    public int JJTUPPER = 61;
    public int JJTTRIMLEADING = 62;
    public int JJTTRIMTRAILING = 63;
    public int JJTTRIMBOTH = 64;
    public int JJTLENGTH = 65;
    public int JJTLOCATE = 66;
    public int JJTABS = 67;
    public int JJTSQRT = 68;
    public int JJTMOD = 69;
    public int JJTSIZE = 70;
    public int JJTCURRENTDATE = 71;
    public int JJTCURRENTTIME = 72;
    public int JJTCURRENTTIMESTAMP = 73;
    public int JJTLIMIT = 74;
    public int JJTSKIPPER = 75;
    public int JJTORDERBY = 76;
    public int JJTORDERBYITEM = 77;
    public int JJTASCENDING = 78;
    public int JJTDESCENDING = 79;
    public int JJTORDERBYEXTENSION = 80;
    public int JJTABSTRACTSCHEMANAME = 81;
    public int JJTTOK = 82;
    public int JJTIDENTIFIER = 83;
    public int JJTIDENTIFICATIONVARIABLE = 84;
    public int JJTINTEGERLITERAL = 85;
    public int JJTDECIMALLITERAL = 86;
    public int JJTBOOLEANLITERAL = 87;
    public int JJTSTRINGLITERAL = 88;
    public int JJTNAMEDINPUTPARAMETER = 89;
    public int JJTPOSITIONALINPUTPARAMETER = 90;
    public int JJTPATTERNVALUE = 91;
    public int JJTESCAPECHARACTER = 92;
    public int JJTTRIMCHARACTER = 93;


    public String[] jjtNodeName = {
            "void",
            "SELECT",
            "UPDATE",
            "DELETE",
            "FROM",
            "FROMITEM",
            "INNERJOIN",
            "OUTERJOIN",
            "OUTERFETCHJOIN",
            "INNERFETCHJOIN",
            "PATH",
            "UPDATEITEM",
            "UPDATEVALUE",
            "SELECTCLAUSE",
            "SELECTEXPRESSIONS",
            "SELECTEXPRESSION",
            "SELECTEXTENSION",
            "CONSTRUCTOR",
            "CLASSNAME",
            "CONSTRUCTORPARAMS",
            "CONSTRUCTORPARAM",
            "AGGREGATE",
            "DISTINCT",
            "DISTINCTPATH",
            "COUNT",
            "AVERAGE",
            "MAX",
            "MIN",
            "SUM",
            "WHERE",
            "GROUPBY",
            "GROUPBYEXTENSION",
            "HAVING",
            "SUBSELECT",
            "OR",
            "AND",
            "NOT",
            "BETWEEN",
            "IN",
            "LIKE",
            "ISNULL",
            "ISEMPTY",
            "MEMBEROF",
            "EXISTS",
            "ANY",
            "ALL",
            "EQUALS",
            "NOTEQUALS",
            "GREATERTHAN",
            "GREATEROREQUAL",
            "LESSTHAN",
            "LESSOREQUAL",
            "ADD",
            "SUBTRACT",
            "MULTIPLY",
            "DIVIDE",
            "NEGATIVE",
            "CONCAT",
            "SUBSTRING",
            "TRIM",
            "LOWER",
            "UPPER",
            "TRIMLEADING",
            "TRIMTRAILING",
            "TRIMBOTH",
            "LENGTH",
            "LOCATE",
            "ABS",
            "SQRT",
            "MOD",
            "SIZE",
            "CURRENTDATE",
            "CURRENTTIME",
            "CURRENTTIMESTAMP",
            "LIMIT",
            "SKIPPER",
            "ORDERBY",
            "ORDERBYITEM",
            "ASCENDING",
            "DESCENDING",
            "ORDERBYEXTENSION",
            "ABSTRACTSCHEMANAME",
            "TOK",
            "IDENTIFIER",
            "IDENTIFICATIONVARIABLE",
            "INTEGERLITERAL",
            "DECIMALLITERAL",
            "BOOLEANLITERAL",
            "STRINGLITERAL",
            "NAMEDINPUTPARAMETER",
            "POSITIONALINPUTPARAMETER",
            "PATTERNVALUE",
            "ESCAPECHARACTER",
            "TRIMCHARACTER",
    };
}
/* JavaCC - OriginalChecksum=5f9f96a45beecae1a9754c535bcc6907 (do not edit this line) */
