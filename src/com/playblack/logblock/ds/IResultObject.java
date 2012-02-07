package com.playblack.logblock.ds;

public interface IResultObject {

	/**
	 * Return the next row in the result set
	 * @return
	 */
	public Object nextRow();
	
	/**
	 * Return the result set we're wrapping.
	 * @return
	 */
	public Object getResultSet();
	
	/**
	 * Check if the result is empty
	 * @return
	 */
	public boolean isEmpty();
}
