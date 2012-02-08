package com.playblack.logblock.ds;

import java.sql.Connection;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;

class ExecutionTask implements Runnable {

	Connection conn;
	PlayerWrapper player;
	Vector location;
	IBlock block;
	int size; //or limit, depending on task
	int world;
	/**
	 * Default constructor
	 */
	public ExecutionTask() {
		
	}
	/**
	 * AreaBlockSearch Constructor
	 * @param player
	 * @param origin
	 * @param size
	 * @param world
	 */
	public ExecutionTask(PlayerWrapper player, Vector origin, int size,
			int world, IBlock block, Connection conn) {
		this.player = player;
		this.location = origin;
		this.size = size;
		this.world = world;
		this.conn = conn;
		this.block = block;
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
