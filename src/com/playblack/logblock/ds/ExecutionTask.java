package com.playblack.logblock.ds;

import java.sql.Connection;
import java.util.logging.Logger;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.mcutils.ItemManager;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;

abstract public class ExecutionTask implements Runnable {

	protected Connection conn;
	protected PlayerWrapper player;
	protected Vector location;
	protected IBlock block;
	protected int size; //or limit, depending on tasking
	protected String world;
	protected int dimension;
	protected ItemManager itemManager;
	protected Logger log;
	/**
	 * Default constructor
	 */
	public ExecutionTask() {

	}


}