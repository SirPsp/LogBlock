package com.playblack.logblock.blocks;

import java.util.ArrayList;

public class SignBlock implements IBlock {

	private ArrayList<String> text = new ArrayList<String>(4);
	private byte data = 0;
	private int type = 63;
	private int world;
	
	public SignBlock() {
		
	}
	
	public SignBlock(int type, byte data, int world, ArrayList<String> sign) {
		this.type = type;
		this.data = data;
		this.world = world;
		text = sign;
	}
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
	
	public String getAtLine(int i) {
		if(i > 3) {
			return " ";
		}
		else {
			return text.get(i);
		}
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
