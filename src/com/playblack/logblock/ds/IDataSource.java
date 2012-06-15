package com.playblack.logblock.ds;

import java.sql.Connection;

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
	public void areaBlockSearch(PlayerWrapper player, int size, int dimension, String world, IBlock block);
	
	/**
	 * Executes a new general area stats operation
	 * @param origin
	 * @param size
	 * @param world
	 */
	public void areaStats(PlayerWrapper player, int size, int dimension, String world);
	
	/**
	 * Executes a new stats operation specific to a player
	 * @param size
	 * @param world
	 * @param player
	 */
	public void areaPlayerStats(int size, int dimension, String world, PlayerWrapper player, String playerName);
	
	/**
	 * Executes a new stats operation listing statistics for all blocks in a world
	 * @param world
	 */
	public void worldBlockStats(int dimension, String world, PlayerWrapper player);
	
	/**
	 * Executes a new block history listing operation
	 * @param v
	 * @param world
	 * @param limit
	 */
	public void getBlockHistory(PlayerWrapper player, Vector v, int dimension, String world, int limit);
	
	/**
	 * Starts the thread that dumps stuff into the database
	 * @param delay
	 * @param limit
	 */
	public void startBlockDumper(long delay, int limit);
	
	/**
	 * Add a block to the block queue.
	 * @param player The name of the player who made the action
	 * @param oldBlock
	 * @param newBlock
	 */
	public void queueBlock(String player, IBlock oldBlock, IBlock newBlock, Vector position, String world);
	
	/**
	 * This allows the execution of any runnable task with the thread manager to<br>
	 * keep the threading clean.
	 * @param r
	 */
	public void executeOther(Runnable r);
	
	/**
	 * Schedule a periodic event.
	 * @param r
	 * @param delay
	 */
	public void scheduleEvent(Runnable r, long delay);
	/**
	 * Returns a connection to use outside this manager
	 * @return
	 */
	public Connection getConnection();
	
	/**
	 * Destroy the manager and cancel all running threads
	 */
	public void destroy();
}
