package com.playblack.logblock.blocks;

public class WorldBlock implements IBlock {

	private byte data=0;
	private int type=0;
	private int world=0; //default
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
	public int getWorld() {
		return world;
	}

	@Override
	public void setWorld(int world) {
		this.world = world;
	}

}
