package com.playblack.mcutils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class basically acts like etc.getItem() just within a package.<br>
 * As canarymod can return the list of all items for direct read access,<br>
 * I felt it's not necessary to wrap the whole etc class. Would have been a<br>
 * waste of time and space and probably even performance
 * @author Chris
 *
 */
public class ItemManager {
	private HashMap<String, Integer> items; //this map is not modifiable
	
	/**
	 * Contruct the ItemManager with the items map from canary.<br>
	 * This map is not modifiable and therefore read-only as mandated by canarymod.
	 * @param canaryItemMap
	 */
	public ItemManager(Map<String,Integer> canaryItemMap) {
		items = new HashMap<String, Integer>();
		items.putAll(canaryItemMap);
	}
	
	/**
	 * Returns the ID to an item name (if exists)
	 * @param name
	 * @return int or -1 if item was not found
	 */
	public int getItemId(String name) {
		if(items.containsKey(name)) {
			return items.get(name);
		}
		return -1;
	}
	
	/**
	 * Returns the name of an item ID as string or null if id was invalid
	 * @param itemId
	 * @return String or null if item id was invalid
	 */
	public String getItemName(int itemId) {
		if(items.containsValue(Integer.valueOf(itemId))) {
			for (String name : items.keySet()) {
                if (items.get(name) == itemId) {
                    return name;
                }
            }
		}
		return null;
	}
}
