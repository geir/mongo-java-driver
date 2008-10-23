package org.mongo.driver.admin;

/**
 * Admin interface for a DB
 */
public interface DBAdmin {

    enum LEVEL {  OFF, SLOW_ONLY, ALL }

    /**
     * Gets the profiling level for the given database - all queries will
     * @return current profiling level
     */
    public int getProfilingLevel();

    /**
     * Sets the a new profiling level
     *
     * @param level new profiling level
     */
    public void setProfilingLevel(LEVEL level);
}
