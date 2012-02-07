package com.playblack.logblock.blocks;

import java.util.LinkedHashMap;
import java.util.Map;

import com.playblack.mcutils.Vector;

/**
 * This is an Object wrapping a a <Vector,IBlock> HashMap,
 * providing a couple of useful helpers
 * @author Chris
 *
 */
public class BlockList {
	private LinkedHashMap<Vector,IBlock> list;
	
	public BlockList() {
		this.list = new LinkedHashMap<Vector,IBlock>();
	}
	
	/**
	 * Add a new Block to this list.
	 * @param v
	 * @param block
	 */
	public void addBlock(Vector v, IBlock block) {
		list.put(v, block);
	}
	
	/**
	 * Add a whole mlist of blocks to this list
	 * @param list
	 */
	public void addBlockList(Map<Vector,IBlock> list) {
		this.list.putAll(list);
	}
	
	/**
	 * Get a block by vector. Returns null if there is no fitting block
	 * @param v
	 * @return IBlock
	 */
	public IBlock getBlockAt(Vector v) {
		return list.get(v);
	}
	
	/**
	 * Get block by coordinates. Returns null if there is no fitting block
	 * @param x
	 * @param y
	 * @param z
	 * @return IBlock
	 */
	public IBlock getBlockAt(int x, int y, int z) {
		return list.get(new Vector(x, y, z));
	}
	
	
}
