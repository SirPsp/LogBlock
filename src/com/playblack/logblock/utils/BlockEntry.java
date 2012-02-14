package com.playblack.logblock.utils;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.mcutils.Vector;

/**
 * This class contains "a row of data", namely a block id, the block
 * that was on its position before, block coords, player who changed it
 * and some other useful stuff
 * @author Chris
 *
 */
public class BlockEntry {
	public String player;
	public IBlock newBlock; //type
	public IBlock oldBlock; //replaced
	public Vector position;
	public String extra = null; //Sign text et cetera
	
	public BlockEntry(String player, IBlock newBlock, IBlock oldBlock, Vector position) {
		this.player = player;
		this.newBlock = newBlock;
		this.oldBlock = oldBlock;
		this.position = position;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BlockEntry for \n")
		.append("old block: ").append(oldBlock.getType()).append("\n")
		.append("new block: ").append(newBlock.getType()).append("\n")
		.append("in world: ").append(oldBlock.getWorld());
		return sb.toString();
	}
	

}
