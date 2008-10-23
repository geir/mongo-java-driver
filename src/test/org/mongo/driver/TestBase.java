package org.mongo.driver;

public class TestBase {

    int cursorCount(DBCursor c) {

        int i = 0;
        for (MongoDoc d : c) {
            i++;
        }

        return i;
    }

}
