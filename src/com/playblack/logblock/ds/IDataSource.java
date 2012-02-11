package com.playblack.logblock.ds;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;

/**
 * This is an interface for data source objects to unify them
 * on the front-end side of the code
 * @author Chris
 *
 */
public interface IDataSource {
	
	/**
	 * Executes a new area block search operation
	 * @param player
	 * @param size
	 * @param world
	 * @param block
	 */
	public void areaBlockSearch(PlayerWrapper player, int size, int world, IBlock block);
	
	/**
	 * Executes a new general area stats operation
	 * @param origin
	 * @param size
	 * @param world
	 */
	public void areaStats(PlayerWrapper player, int size, int world);
	
	/**
	 * Executes a new stats operation specific to a player
	 * @param size
	 * @param world
	 * @param player
	 */
	public void areaPlayerStats(int size, int world, PlayerWrapper player, String playerName);
	
	/**
	 * Executes a new stats operation listing statistics for all blocks in a world
	 * @param world
	 */
	public void worldBlockStats(int world, PlayerWrapper player);
	
	/**
	 * Executes a new block history listing operation
	 * @param v
	 * @param world
	 * @param limit
	 */
	public void getBlockHistory(PlayerWrapper player, Vector v, int world, int limit);
	
	/**
	 * Takes a list of blocks and dumps them into the database
	 * TODO: Add block list object etc
	 */
	public void processBlockList(int delay, int limit);
}
