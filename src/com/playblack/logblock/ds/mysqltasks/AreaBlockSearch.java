package com.playblack.logblock.ds.mysqltasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.ds.ExecutionTask;
import com.playblack.logblock.utils.LogBlockConfig;
import com.playblack.mcutils.ColorManager;
import com.playblack.mcutils.ItemManager;
import com.playblack.mcutils.PlayerWrapper;


public class AreaBlockSearch extends ExecutionTask {
	/**
	 * AreaBlockSearch Constructor
	 * @param player
	 * @param origin
	 * @param size
	 * @param world
	 */
	public AreaBlockSearch(PlayerWrapper player, int size, int world, IBlock block, Connection conn, ItemManager im, Logger log) {
		this.player = player;
		this.location = player.getLocation(); //so awesome :D
		this.size = size;
		this.world = world;
		this.conn = conn;
		this.block = block;
		this.log = log;
		this.itemManager = im;
		
	}

	@Override
	public void run() {
		boolean hist = false;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    SimpleDateFormat formatter = new SimpleDateFormat(LogBlockConfig.getDateFormat());
	    try
	    {
	      this.conn.setAutoCommit(false);
	      ps = this.conn.prepareStatement("SELECT * from blocks where (type = ? or replaced = ?) and y > ? and y < ? and x > ? and x < ? and z > ? and z < ? AND world = ? order by date desc limit 10", 1);
	      ps.setInt(1, this.block.getType());
	      ps.setInt(2, this.block.getType());
	      ps.setInt(3, (int)this.location.getY() - this.size);
	      ps.setInt(4, (int)this.location.getY() + this.size);
	      ps.setInt(5, (int)this.location.getX() - this.size);
	      ps.setInt(6, (int)this.location.getX() + this.size);
	      ps.setInt(7, (int)this.location.getZ() - this.size);
	      ps.setInt(8, (int)this.location.getZ() + this.size);
	      ps.setInt(9, this.world);
	      rs = ps.executeQuery();

	      this.player.sendMessage(ColorManager.LightBlue+"Block history within " + this.size + " blocks of  " + (int)this.location.getX() + ", " + (int)this.location.getY() + ", " + (int)this.location.getZ() + ": ");

	      while (rs.next())
	      {
	        Timestamp date = rs.getTimestamp("date");
	        String datestr = formatter.format(date);
	        StringBuilder msg = new StringBuilder();
	        
	        msg.append(datestr).append(" ").append(rs.getString("player"))
	        .append("( ").append(rs.getInt("x")).append(" + ")
	        .append(rs.getInt("y")).append(" + ")
	        .append(rs.getInt("z")).append(" )");

	        if (rs.getInt("type") == 0) {
	        	msg.append("destroyed ").append(itemManager.getItemName(rs.getInt("replaced")));
	        }
	          
	        else if (rs.getInt("replaced") == 0) {
	        	msg.append("created ").append(itemManager.getItemName(rs.getInt("type")));
	        }
	          
	        else {
	        	msg.append("replaced ").append(itemManager.getItemName(rs.getInt("replaced"))).append(" with ").append(itemManager.getItemName(rs.getInt("type")));
	        }  
	        player.sendMessage(ColorManager.prependColor(msg, ColorManager.Gold).toString());
	        hist = true;
	      }
	    } catch (SQLException ex) {
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
	    if (!hist)
	    player.sendMessage(ColorManager.prependColor(new StringBuilder(), ColorManager.LightBlue).append("None").toString());
		
	}
}
