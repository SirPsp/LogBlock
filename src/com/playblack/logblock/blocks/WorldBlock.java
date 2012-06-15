package com.playblack.logblock.blocks;

public class WorldBlock implements IBlock {

	private byte data=0;
	private int type=0;
	private String world;
	private int dimension=0; //default
	public WorldBlock(int type, int data, int dimension, String world) {
		this.type = type;
		this.data = (byte)data;
		this.world = world;
		this.dimension = dimension;
	}
	
	public WorldBlock(int type, int data) {
		this.type = type;
		this.data = (byte)data;
	}
	
	
	@Override
	public byte getData() {
		return data;
	}

	@Override
	public void setData(byte data) {
		this.data = data;

	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String getWorld() {
		return world;
	}

	@Override
	public void setWorld(String world) {
		this.world = world;
	}
	
	public String toString() {
    	return  "BLOCK\n  Type: "+type+"\n"+
    			", Data: "+data+"\n"+
    			", World: "+world+"\n"+
    			"===================================\n";
	}

	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public void setDimension(int dim) {
		this.dimension = dim;
	}

}
