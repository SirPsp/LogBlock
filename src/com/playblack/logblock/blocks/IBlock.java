package com.playblack.logblock.blocks;

public interface IBlock {
	
	/**
	 * Get the data value for this block.
	 * @return
	 */
	public byte getData();
	
	/**
	 * Set the data value for this block
	 * @param data
	 */
	public void setData(byte data);
	
	/**
	 * Get the type of this block
	 * @return
	 */
	public int getType();
	
	/**
	 * Set the type of this block
	 * @param type
	 */
	public void setType(int type);
	
	/**
	 * Get the world ID for this block
	 * @return
	 */
	public int getWorld();
	
	/**
	 * Set the world ID for this block
	 * @param world
	 */
	public void setWorld(int world);
}
