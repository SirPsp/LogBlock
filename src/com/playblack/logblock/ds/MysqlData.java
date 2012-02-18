package com.playblack.logblock.ds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.blocks.SignBlock;
import com.playblack.logblock.blocks.WorldBlock;
import com.playblack.logblock.ds.mysqltasks.*;
import com.playblack.logblock.ds.services.ConnectionService;
import com.playblack.logblock.utils.BlockEntry;
import com.playblack.mcutils.ColorManager;
import com.playblack.mcutils.ItemManager;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;

/**
 * This class is a thread and connection manager. From here, all the operations of LogBlock<br>
 * are executed by a ScheduledExecutorService and connections are managed by a ConnectionService.<br> 
 * This will optimize the threading introduced in the original LogBlock a bit and also keeps the "front end code" clean.<br>
 * in the end we will only need to call this class and the method we need, the rest will<br>
 * be taken care of here, in the code behind the code :P
 * @author Chris
 *
 */
public class MysqlData implements IDataSource {
	
	ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	ConnectionService connectionPool;
	ItemManager itemManager;
	Logger log;
	/**
	 * This is a queue holding all currently logged blocks. It will be consumed by
	 * the processBlocklist function.
	 */
	public static LinkedBlockingQueue<BlockEntry> blockList = new LinkedBlockingQueue<BlockEntry>();
	
	/**
	 * Default constructor providing a set-up connection service, an ItemManager and a Logger.
	 * @param cp
	 * @param im
	 * @param l
	 */
	public MysqlData(ConnectionService cp, ItemManager im, Logger l) {
		connectionPool = cp;
		itemManager = im;
		log = l;
	}

	@Override
	public void areaBlockSearch(PlayerWrapper player, int size, int world, IBlock block) {
		try {
			threadPool.execute(new AreaBlockSearch(player, size, world, block, connectionPool.getConnection(), itemManager, log));
		} 
		catch (SQLException e) {
			log.warning("LogBlock cannot execute Task: AreaBlockSearch -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: AreaBlockSearch (SQLException)");
			e.printStackTrace();
		} 
		catch(RejectedExecutionException e) {
			log.warning("LogBlock cannot execute Task: AreaBlockSearch -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: AreaBlockSearch (Execution rejected)");
			player.sendMessage(ColorManager.Navy + "Please try again shortly, the thread manager is busy.");
			e.printStackTrace();
		}
	}

	@Override
	public void areaStats(PlayerWrapper player, int size, int world) {
		try {
			threadPool.execute(new AreaStats(player, size, world, connectionPool.getConnection()));
		} 
		catch (SQLException e) {
			log.warning("LogBlock cannot execute Task: AreaStats -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: AreaStats (SQLException)");
			e.printStackTrace();
		} 
		catch(RejectedExecutionException e) {
			log.warning("LogBlock cannot execute Task: AreaStats -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: AreaStats (Execution rejected)");
			player.sendMessage(ColorManager.Navy + "Please try again shortly, the thread manager is busy.");
			e.printStackTrace();
		}

	}

	@Override
	public void areaPlayerStats(int size, int world, PlayerWrapper player, String playerName) {
		
		try {
			threadPool.execute(new AreaPlayerStats(connectionPool.getConnection(), player, playerName, size, itemManager, log, world));
		} 
		catch (SQLException e) {
			log.warning("LogBlock cannot execute Task: AreaPlayerStats -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: AreaPlayerStats (SQLException)");
			e.printStackTrace();
		} 
		catch(RejectedExecutionException e) {
			log.warning("LogBlock cannot execute Task: AreaPlayerStats -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: AreaPlayerStats (Execution rejected)");
			player.sendMessage(ColorManager.Navy + "Please try again shortly, the thread manager is busy.");
			e.printStackTrace();
		}
		
	}

	@Override
	public void worldBlockStats(int world, PlayerWrapper player) {
		try {
			threadPool.execute(new WorldBlockStats(player, connectionPool.getConnection(), log, world));
		} 
		catch (SQLException e) {
			log.warning("LogBlock cannot execute Task: WorldBlockStats -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: WorldBlockStats (SQLException)");
			e.printStackTrace();
		} 
		catch(RejectedExecutionException e) {
			log.warning("LogBlock cannot execute Task: WorldBlockStats -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: WorldBlockStats (Execution rejected)");
			player.sendMessage(ColorManager.Navy + "Please try again shortly, the thread manager is busy.");
			e.printStackTrace();
		}
		
	}

	@Override
	public void getBlockHistory(PlayerWrapper player, Vector v, int world, int limit) {
		try {
			threadPool.execute(new BlockHistory(player, connectionPool.getConnection(), log, world, v, itemManager));
		} 
		catch (SQLException e) {
			log.warning("LogBlock cannot execute Task: BlockHistory -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: BlockHistory (SQLException)");
			e.printStackTrace();
		} 
		catch(RejectedExecutionException e) {
			log.warning("LogBlock cannot execute Task: BlockHistory -  "+e.getMessage());
			player.notify("LogBlock cannot execute task: BlockHistory (Execution rejected)");
			player.sendMessage(ColorManager.Navy + "Please try again shortly, the thread manager is busy.");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void startBlockDumper(long delay, int limit) {
		try {
			threadPool.scheduleAtFixedRate(new DumpBlockEntries(log, limit, connectionPool), 0, delay, TimeUnit.SECONDS);
		} 
		catch(RejectedExecutionException e) {
			log.warning("LogBlock could not start BlockDumper Thread! This is highly crappy! "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void queueBlock(String player, IBlock oldBlock, IBlock newBlock, Vector position) {
		if(oldBlock == null) {
			oldBlock = new WorldBlock(0,0,0);
		}
		if(newBlock == null) {
			newBlock = new WorldBlock(0,0,0);
		}

		BlockEntry be = new BlockEntry(player, newBlock, oldBlock, position);
		if(newBlock instanceof SignBlock) {
			String text = "sign";
			for (int i = 0; i < 4; i++)
				text = text + " [" + ((SignBlock)newBlock).getAtLine(i) + "]";
			be.extra = text;
		}
		else if(oldBlock instanceof SignBlock) {
			String text = "sign";
			for (int i = 0; i < 4; i++)
				text = text + " [" + ((SignBlock)oldBlock).getAtLine(i) + "]";
			be.extra = text;
		}
		if(blockList.offer(be)) {
			
		}
		else {
			//insanity!!!
			log.warning("LogBlock: Failed to insert new Block " +
					"Entry for player "+player+" (queue is full ... " +
					"wtf man, that's crazy shit!)");
		}
	}
	
	@Override
	public void executeOther(Runnable r) {
		try {
			threadPool.execute(r);
		}
		catch(RejectedExecutionException e) {
			log.warning("LogBlock could not execute a non-standard task (ie. Rollback), Execution rejected."+e.getMessage());
		}
	}
	
	@Override
	public void scheduleEvent(Runnable r, long delay) {
		threadPool.scheduleAtFixedRate(r, 0, delay, TimeUnit.SECONDS);
	}
	
	public Connection getConnection() {
		try {
			return connectionPool.getConnection();
		} catch (SQLException e) {
			e.getStackTrace();
			return null;
		}
	}
	
	@Override
	public void destroy() {
		threadPool.shutdownNow();
	}
}
