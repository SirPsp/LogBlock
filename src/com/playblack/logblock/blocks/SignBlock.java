package com.playblack.logblock.blocks;

import java.util.ArrayList;

public class SignBlock extends WorldBlock {

	private ArrayList<String> text = new ArrayList<String>(4);
	
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
	 * Get the sign text as array. each line is one index
	 * @return
	 */
	public String[] getTextArray() {
		return (String[]) text.toArray();
	}

}
