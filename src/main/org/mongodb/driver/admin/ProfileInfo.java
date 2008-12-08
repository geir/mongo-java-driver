package org.mongodb.driver.admin;

import java.util.Date;

/**
 *  Holder for profiling information from a query
 */
public class ProfileInfo {

    protected final String _query;
    protected final long _timeInMillis;
    protected final Date _timestamp;

    public ProfileInfo(String q, long time, Date ts) {
        _query = q;
        _timeInMillis = time;
        _timestamp = new Date(ts.getTime());
    }

    public String getQuery() {
        return _query;
    }

    public long getTimeInMillis() {
        return _timeInMillis;
    }

    public Date getTimestamp() {
        return new Date(_timestamp.getTime());
    }
}
