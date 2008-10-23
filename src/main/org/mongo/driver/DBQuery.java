package org.mongo.driver;

import java.util.Map;

/**
 *  A query for Mongo
 */
public class DBQuery  {
    
    protected int _numberToSkip;      // number of elements in the result set to skip to start
    protected int _numberToReturn;    // number of elements to return in first response - if < 0, then that means "limit"
    protected MongoSelector _querySelector;     // "object format" query
    protected MongoSelector _retFieldsDoc; // fields to populate in returned query
    protected MongoSelector _orderBy;      // this needs to be an array


    public DBQuery() {
        this(new MongoSelector(), null, 0, 0);
    }
    
    public DBQuery(MongoSelector query) {
        this(query, null, 0, 0);
    }

    public DBQuery(Map queryMap) throws MongoDBException {
        this(new MongoSelector(queryMap), null, 0, 0);
    }

    public DBQuery(MongoSelector query, MongoSelector fields, int nSkip, int nReturn) {
        setQuerySelector(query);
        setReturnFieldsSelector(fields);
        setNumberToSkip(nSkip);
        setNumberToReturn(nReturn);
    }

    public int getNumberToSkip() {
        return _numberToSkip;
    }

    public void setNumberToSkip(int nSkip) {
        _numberToSkip = nSkip;
    }

    public int getNumberToReturn() {
        return _numberToReturn;
    }

    public void setNumberToReturn(int nReturn) {
        _numberToReturn = nReturn;
    }

    public MongoDoc getQuerySelector() {
        return _querySelector;
    }

    public void setQuerySelector(MongoSelector selector) {
        _querySelector = selector;
    }

    public MongoSelector getReturnFieldsSelector() {
        return _retFieldsDoc;
    }

    public void setReturnFieldsSelector(MongoSelector selector) {
        _retFieldsDoc = selector;
    }

    public MongoSelector getOrderBy() {
        return _orderBy;
    }

    public void setOrderBy(MongoSelector selector) {
        _orderBy = selector;
    }
}
