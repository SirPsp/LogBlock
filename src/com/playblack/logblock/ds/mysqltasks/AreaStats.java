package com.playblack.logblock.ds.mysqltasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import com.playblack.logblock.ds.ExecutionTask;
import com.playblack.mcutils.ColorManager;
import com.playblack.mcutils.PlayerWrapper;

public class AreaStats extends ExecutionTask {

	public AreaStats(PlayerWrapper p, int size, int dimension, String world, Connection conn) {
		this.size = size;
		this.world = world;
		this.dimension = dimension;
		this.player = p;
		this.location = p.getLocation();
		this.conn = conn;
	}
	@Override
	public void run() {
		HashSet<String> players = new HashSet<String>();
	    HashMap<String, Integer> created = new HashMap<String, Integer>();
	    HashMap<String, Integer> destroyed = new HashMap<String, Integer>();

	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    try
	    {
	      this.conn.setAutoCommit(false);
	      ps = this.conn.prepareStatement("SELECT player, count(player) as num from blocks where type > 0 and y > ? and y < ? and x > ? and x < ? and z > ? and z < ? AND dimension = ? AND world = ? group by player order by count(player) desc limit 10", 1);
	      ps.setInt(1, (int)this.location.getY() - this.size);
	      ps.setInt(2, (int)this.location.getY() + this.size);
	      ps.setInt(3, (int)this.location.getX() - this.size);
	      ps.setInt(4, (int)this.location.getX() + this.size);
	      ps.setInt(5, (int)this.location.getZ() - this.size);
	      ps.setInt(6, (int)this.location.getZ() + this.size);
	      ps.setInt(7, this.dimension);
	      ps.setString(8, this.world);
	      rs = ps.executeQuery();
	      while (rs.next())
	      {
	        players.add(rs.getString("player"));
	        created.put(rs.getString("player"), Integer.valueOf(rs.getInt("num")));
	      }
	      rs.close();
	      ps.close();

	      ps = this.conn.prepareStatement("SELECT player, count(player) as num from blocks where replaced > 0 and y > ? and y < ? and x > ? and x < ? and z > ? and z < ? AND dimension =? AND world = ? group by player order by count(player) desc limit 10", 1);
	      ps.setInt(1, (int)this.location.getY() - this.size);
	      ps.setInt(2, (int)this.location.getY() + this.size);
	      ps.setInt(3, (int)this.location.getX() - this.size);
	      ps.setInt(4, (int)this.location.getX() + this.size);
	      ps.setInt(5, (int)this.location.getZ() - this.size);
	      ps.setInt(6, (int)this.location.getZ() + this.size);
	      ps.setInt(7, this.dimension);
	      ps.setString(8, this.world);
	      rs = ps.executeQuery();
	      while (rs.next())
	      {
	        players.add(rs.getString("player"));
	        destroyed.put(rs.getString("player"), Integer.valueOf(rs.getInt("num")));
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

	    this.player.sendMessage(ColorManager.LightBlue + "Within " + this.size + " blocks of you: ");
	    if (players.size() == 0)
	    {
	      this.player.sendMessage(ColorManager.LightBlue + "No results found.");
	      return;
	    }

	    this.player.sendMessage(ColorManager.Gold + String.format("%-6s %-6s %s", new Object[] { "Creat", "Destr", "Player" }));
	    for (String p : players)
	    {
	      Integer c = (Integer)created.get(p);
	      Integer d = (Integer)destroyed.get(p);
	      if (c == null)
	        c = Integer.valueOf(0);
	      if (d == null)
	        d = Integer.valueOf(0);
	      this.player.sendMessage(ColorManager.Gold + String.format("%-6d %-6d %s", new Object[] { c, d, p }));
	    }
	}

}
