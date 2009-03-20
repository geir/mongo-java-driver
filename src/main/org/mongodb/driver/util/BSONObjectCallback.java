package org.mongodb.driver.util;

/**
 *  Interface to allow callbacks from the decoding process.
 */
public interface BSONObjectCallback {

    /**
     *  Determines if an object should be deserialized as a Doc or a BSONBytes
     *
     * @param key key of the object which is about to be decoded
     * @return true if the object should remain as BSON, false if it should be decoded as a Doc
     */
    public boolean deserializeObjectAsBSON(String key);
}
