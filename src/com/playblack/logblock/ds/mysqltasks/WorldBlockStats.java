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
import com.playblack.mcutils.ColorManager;
import com.playblack.mcutils.PlayerWrapper;

public class WorldBlockStats extends ExecutionTask {

	public WorldBlockStats(PlayerWrapper p, Connection conn, Logger log, int world) {
		this.conn = conn;
		this.player = p;
		this.log = log;
		this.world = world;
		
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
	      ps = this.conn.prepareStatement("SELECT player, count(player) as num from blocks where type > 0 AND world = ? group by player order by count(player) desc limit 5", 1);
	      ps.setInt(1, this.world);
	      rs = ps.executeQuery();
	      while (rs.next())
	      {
	        players.add(rs.getString("player"));
	        created.put(rs.getString("player"), Integer.valueOf(rs.getInt("num")));
	      }
	      rs.close();
	      ps.close();

	      ps = this.conn.prepareStatement("SELECT player, count(player) as num from blocks where replaced > 0 AND world = ? group by player order by count(player) desc limit 5", 1);
	      ps.setInt(1, this.world);
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

	    this.player.sendMessage(ColorManager.Navy + "Within entire world:");
	    if (players.size() == 0)
	    {
	      this.player.sendMessage(ColorManager.Navy + "No results found.");
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
