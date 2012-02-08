package com.playblack.mcutils;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This a class that contains a reflected Player class from canarymod.<br>
 * It's a workaround in order to bring some basic player functions into packages
 * @author Chris
 *
 */
public class PlayerWrapper {
	Object player;
	Logger log;
	Object[] voidObject = new Object[0];
	/**
	 * Default constructor, we always need a log
	 */
	public PlayerWrapper(Logger log) {
		this.log = log;
	}
	
	/**
	 * Constructor that sets the player to wrap.
	 * @param player The object that is a player
	 */
	public PlayerWrapper(Object player, Logger log) {
		this.player = player;
		this.log = log;
	}
	
	/**
	 * make the wrapped player chat
	 * @param message
	 */
	public void chat(String message)  {
		try {
			
			player.getClass().getMethod("chat", String.class).invoke(player, message);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			log.log(Level.INFO, "LogBlock: SecurityException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.log(Level.INFO, "LogBlock: IllegalAccessException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.log(Level.INFO, "LogBlock: InvocationTargetException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.log(Level.INFO, "LogBlock: NoSuchMethodException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the wrapped player a message
	 * @param message
	 */
	public void sendMessage(String message) {
		try {
			player.getClass().getMethod("sendMessage", String.class).invoke(player, message);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			log.log(Level.INFO, "LogBlock: SecurityException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.log(Level.INFO, "LogBlock: IllegalAccessException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.log(Level.INFO, "LogBlock: InvocationTargetException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.log(Level.INFO, "LogBlock: NoSuchMethodException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the name of the wrapped player
	 * @param message
	 */
	public String getName() {
		try {
			return (String) player.getClass().getMethod("getName", (Class<?>)null).invoke(player, voidObject);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			log.log(Level.INFO, "LogBlock: SecurityException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.log(Level.INFO, "LogBlock: IllegalAccessException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.log(Level.INFO, "LogBlock: InvocationTargetException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.log(Level.INFO, "LogBlock: NoSuchMethodException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns player location as vector or null if somethign went wrong
	 * @return
	 */
	public Vector getLocation() {
		Vector v = null;
		try {
			int x = (Integer) player.getClass().getMethod("getX", (Class<?>)null).invoke(player, voidObject);
			int y = (Integer) player.getClass().getMethod("getY", (Class<?>)null).invoke(player, voidObject);
			int z = (Integer) player.getClass().getMethod("getZ", (Class<?>)null).invoke(player, voidObject);
			
			v = new Vector(x,y,z);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			log.log(Level.INFO, "LogBlock: SecurityException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.log(Level.INFO, "LogBlock: IllegalAccessException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.log(Level.INFO, "LogBlock: InvocationTargetException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.log(Level.INFO, "LogBlock: NoSuchMethodException in PlayerWrapper: "+e.getMessage());
			e.printStackTrace();
		}
		return v;
	}
}
