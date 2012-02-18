package com.playblack.logblock.ds.mysqltasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.ds.ExecutionTask;
import com.playblack.mcutils.ItemManager;
import com.playblack.mcutils.PlayerWrapper;

public class AreaPlayerStats extends ExecutionTask {

	String name;
	
	public AreaPlayerStats(Connection conn, PlayerWrapper player, String name, int size, ItemManager im, Logger log, int world)
	  {
	    this.player = player;
	    this.location = player.getLocation();
	    this.name = name;
	    this.size = size;
	    this.conn = conn;
	    this.itemManager = im;
	    this.log = log;
	    this.world = world;
	  }
	@Override
	public void run() {
		HashSet<String> types = new HashSet<String>();
	    HashMap<String, Integer> created = new HashMap<String, Integer>();
	    HashMap<String, Integer> destroyed = new HashMap<String, Integer>();

	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    try
	    {
	      this.conn.setAutoCommit(false);
	      ps = this.conn.prepareStatement("SELECT type, count(type) as num from blocks where type > 0 and player = ? and y > 0 and x > ? and x < ? and z > ? and z < ? AND world = ? group by type order by count(replaced) desc limit 10", 1);
	      ps.setString(1, this.name);
	      ps.setInt(2, (int)this.location.getX() - this.size);
	      ps.setInt(3, (int)this.location.getX() + this.size);
	      ps.setInt(4, (int)this.location.getZ() - this.size);
	      ps.setInt(5, (int)this.location.getZ() + this.size);
	      ps.setInt(6, this.world);
	      rs = ps.executeQuery();
	      while (rs.next())
	      {
	    	  types.add(itemManager.getItemName(rs.getInt("type")));
	    	  created.put(itemManager.getItemName(rs.getInt("type")), Integer.valueOf(rs.getInt("num")));
//	        types.add(etc.getDataSource().getItem(rs.getInt("type")));
//		        created.put(etc.getDataSource().getItem(rs.getInt("type")), Integer.valueOf(rs.getInt("num")));
	      }
	      rs.close();
	      ps.close();

	      ps = this.conn.prepareStatement("SELECT replaced, count(replaced) as num from blocks where replaced > 0 and player = ? and y > 0 and x > ? and x < ? and z > ? and z < ? AND world = ? group by replaced order by count(replaced) desc limit 10", 1);
	      ps.setString(1, this.name);
	      ps.setInt(2, (int)this.location.getX() - this.size);
	      ps.setInt(3, (int)this.location.getX() + this.size);
	      ps.setInt(4, (int)this.location.getZ() - this.size);
	      ps.setInt(5, (int)this.location.getZ() + this.size);
	      ps.setInt(6, this.world);
	      rs = ps.executeQuery();
	      while (rs.next())
	      {
	    	  types.add(itemManager.getItemName(rs.getInt("replaced")));
	    	  created.put(itemManager.getItemName(rs.getInt("replaced")), Integer.valueOf(rs.getInt("num")));
//	        types.add(etc.getDataSource().getItem(rs.getInt("replaced")));
//	        destroyed.put(etc.getDataSource().getItem(rs.getInt("replaced")), Integer.valueOf(rs.getInt("num")));
	      }
	    }
	    catch (SQLException ex) {
	      log.log(Level.SEVERE, getClass().getName() + " SQL exception", ex);
	    } finally {
	      try {
	        if (rs != null)
	          rs.close();
	        if (ps != null)
	          ps.close();
	        if (conn != null) {
				if(!conn.isClosed()) {
					conn.close();
				}
			}
	      } catch (SQLException ex) {
	        log.log(Level.SEVERE, getClass().getName() + " SQL exception on close", ex);
	      }
	    }

	    this.player.sendMessage("§3Player " + this.name + " within " + this.size + " blocks of you: ");
	    if (types.size() == 0)
	    {
	      this.player.sendMessage("§3No results found.");
	      return;
	    }

	    this.player.sendMessage("§6" + String.format("%-6s %-6s %s", new Object[] { "Creat", "Destr", "Block" }));
	    for (String t : types)
	    {
	      Integer c = (Integer)created.get(t);
	      Integer d = (Integer)destroyed.get(t);
	      if (c == null)
	        c = Integer.valueOf(0);
	      if (d == null)
	        d = Integer.valueOf(0);
	      this.player.sendMessage("§6" + String.format("%-6d %-6d %s", new Object[] { c, d, t }));
	    }

	}

}
