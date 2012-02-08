package com.playblack.logblock.ds;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;


public interface IDataSource {
	
	/**
	 * Returns an IResultObject containing blocks of "type" changed by "player" within the x,y,z bounds of Vector + size in world.
	 * @param player
	 * @param origin
	 * @param size
	 * @param world
	 * @return IResultObject
	 */
	public IResultObject areaBlockSearch(PlayerWrapper player, Vector origin, int size, int world, IBlock block);
	
	/**
	 * Returns an IResultObject containing a summary of how many blocks have been destroyed and created by what players
	 * @param origin
	 * @param size
	 * @param world
	 * @return IResultObject
	 */
	public IResultObject areaStats(Vector origin, int size, int world);
	
	/**
	 * Returns an IResultObject containing a summary of how many blocks have been destroyed and created by a certain player
	 * @param origin
	 * @param size
	 * @param world
	 * @param player
	 * @return IResultObject
	 */
	public IResultObject areaPlayerStats(Vector origin, int size, int world, PlayerWrapper player);
	
	/**
	 * Returns an IResultObject containing a summary of created/destroyed blocks for all players in a world
	 * @param world
	 * @return IResultObject
	 */
	public IResultObject worldBlockStats(int world);
	
	/**
	 * Returns an IResultObject containing the last x points of history of this block position this block
	 * @param v
	 * @param world
	 * @param limit
	 * @return IResultObject
	 */
	public IResultObject getBlockHistory(Vector v, int world, int limit);
	
	public void processBlockList();
}
