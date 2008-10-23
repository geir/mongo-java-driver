package org.mongo.driver.options.impl;

import org.mongo.driver.MongoSelector;

import java.util.ArrayList;

/**
 * Marker interface for DB options
 */
public class MongoOptions extends ArrayList<MongoOption> {

    public void addOption(DBOption option) {

        if (option == null) {
            return;
        }

        addOption(option);
    }
}
