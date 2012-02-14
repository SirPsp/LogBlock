package com.playblack.logblock.blocks;

import java.util.ArrayList;

public class SignBlock implements IBlock {

	private ArrayList<String> text = new ArrayList<String>(4);
	private byte data;
	private int type;
	private int world;
	
	/**
	 * Set the text of this sign
	 * @param texts
	 */
	public void setText(String[] texts) {
		for(String line : texts) {
			text.add(line);
		}
	}
	
	/**
	 * Set the text of this sign
	 * @param texts
	 */
	public void setText(ArrayList<String> texts) {
		this.text = texts;
	}
	
	/**
	 * Get the sign text as array. each line is one index
	 * @return
	 */
	public String[] getTextArray() {
		return (String[]) text.toArray();
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
	public int getWorld() {
		return world;
	}

	@Override
	public void setWorld(int world) {
		this.world = world;
		
	}

}
