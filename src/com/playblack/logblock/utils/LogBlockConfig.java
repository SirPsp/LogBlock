package com.playblack.logblock.utils;

public class LogBlockConfig {
	private String dbDriver;
	private String dbPassword;
	private String dbUrl;
	private String dbUsername;
	private int defaultDistance;
	private int queryLimit;
	private int blockToolId;
	private boolean removeToolBlock;
	private int toolId;
	private boolean useCanaryDb;
	private boolean debug;
	private int delay;
	private static String dateFormat = "MM-dd hh:mm:ss";
	
	/**
	 * Get the db driver name
	 * @return
	 */
	public String getDbDriver() {
		return dbDriver;
	}
	/**
	 * Set the db driver name
	 * @return
	 */
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	
	/**
	 * Get the db password
	 * @return
	 */
	public String getDbPassword() {
		return dbPassword;
	}
	
	/**
	 * Set the db password
	 * @return
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	/**
	 * Get the db Url
	 * @return
	 */
	public String getDbUrl() {
		return dbUrl;
	}
	
	/**
	 * Set the db url
	 * @return
	 */
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	
	/**
	 * Get the db user
	 * @return
	 */
	public String getDbUsername() {
		return dbUsername;
	}
	
	/**
	 * Set the db user
	 * @return
	 */
	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	
	/**
	 * Get the default lookup distance
	 * @return
	 */
	public int getDefaultDistance() {
		return defaultDistance;
	}
	
	/**
	 * Set the default lookup distance
	 * @return
	 */
	public void setDefaultDistance(int defaultDistance) {
		this.defaultDistance = defaultDistance;
	}
	
	/**
	 * Get the block tool (check with a block)
	 * @return
	 */
	public int getBlockToolId() {
		return blockToolId;
	}
	
	/**
	 * Set the block tool id
	 * @return
	 */
	public void setBlockToolId(int blockToolId) {
		this.blockToolId = blockToolId;
	}
	
	/**
	 * Get the remove tool id
	 * @return
	 */
	public boolean removeToolBlock() {
		return removeToolBlock;
	}
	
	/**
	 * Set the remove tool id
	 * @return
	 */
	public void setRemoveToolBlock(boolean removeToolBlock) {
		this.removeToolBlock = removeToolBlock;
	}
	
	/**
	 * Get the tool id
	 * @return
	 */
	public int getToolId() {
		return toolId;
	}
	
	/**
	 * Set the tool id
	 * @return
	 */
	public void setToolId(int toolId) {
		this.toolId = toolId;
	}
	
	/**
	 * use canary connection?
	 * @return
	 */
	public boolean useCanaryDb() {
		return useCanaryDb;
	}
	
	/**
	 * set if we want to use canary connection
	 * @return
	 */
	public void setUseCanaryDb(boolean useCanaryDb) {
		this.useCanaryDb = useCanaryDb;
	}
	
	/**
	 * Is debugging on?
	 * @return
	 */
	public boolean isDebug() {
		return debug;
	}
	
	/**
	 * Set if debugging is on
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Get consumer delay
	 * @return
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Set consumer delay
	 * @param delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Get the limit for a query (amount of returned rows per query)
	 * @return
	 */
	public int getQueryLimit() {
		return queryLimit;
	}
	
	/**
	 * Set the limit for a query (amount of returned rows per query)
	 * @return
	 */
	public void setQueryLimit(int queryLimit) {
		this.queryLimit = queryLimit;
	}
	
	/**
	 * Get the currently set date format for formatting timestamps
	 * @return
	 */
	public static String getDateFormat() {
		return dateFormat;
	}
	
	/**
	 * Set the date format for formatting timestamps
	 * @param dateFormat
	 */
	public static void setDateFormat(String dateFormat) {
		LogBlockConfig.dateFormat = dateFormat;
	}
}
