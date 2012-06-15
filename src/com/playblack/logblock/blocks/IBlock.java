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
	 * Get the dimension for this block
	 * @return
	 */
	public int getDimension();
	
	/**
	 * Set the dimension for this block
	 * @param dimension
	 */
	public void setDimension(int dim);
	
	/**
	 * Get the world for this block
	 * @return
	 */
	public String getWorld();
	
	/**
	 * Set the world for this block
	 * @param world
	 */
	public void setWorld(String world);
}
