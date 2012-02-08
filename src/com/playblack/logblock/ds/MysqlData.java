package com.playblack.logblock.ds;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.ds.services.ConnectionService;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;

public class MysqlData implements IDataSource {
	
	ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	ConnectionService connectionPool;

	@Override
	public IResultObject areaBlockSearch(PlayerWrapper player, Vector origin, int size, int world, IBlock block) {
		try {
			threadPool.execute(new ExecutionTask(player, origin, size, world, block, connectionPool.getConnection()) {
				
				@Override
				public void run() {
					boolean hist = false;
				    PreparedStatement ps = null;
				    ResultSet rs = null;

				    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd hh:mm:ss");
				    try
				    {
				      this.conn.setAutoCommit(false);
				      ps = this.conn.prepareStatement("SELECT * from blocks where (type = ? or replaced = ?) and y > ? and y < ? and x > ? and x < ? and z > ? and z < ? order by date desc limit 10", 1);
				      ps.setInt(1, this.block.getType());
				      ps.setInt(2, this.block.getType());
				      ps.setInt(3, (int)this.location.getY() - this.size);
				      ps.setInt(4, (int)this.location.getY() + this.size);
				      ps.setInt(5, (int)this.location.getX() - this.size);
				      ps.setInt(6, (int)this.location.getX() + this.size);
				      ps.setInt(7, (int)this.location.getZ() - this.size);
				      ps.setInt(8, (int)this.location.getZ() + this.size);
				      rs = ps.executeQuery();

				      this.player.sendMessage("§3Block history within " + this.size + " blocks of  " + (int)this.location.getX() + ", " + (int)this.location.getY() + ", " + (int)this.location.getZ() + ": ");

				      while (rs.next())
				      {
				        Timestamp date = rs.getTimestamp("date");
				        String datestr = formatter.format(date);
				        String msg = datestr + " " + rs.getString("player") + " (" + rs.getInt("x") + ", " + rs.getInt("y") + ", " + rs.getInt("z") + ") ";
				        
				        //TODO:
				        //Fix etc and logs
				        if (rs.getInt("type") == 0) {
				        	//msg = msg + "destroyed " + etc.getDataSource().getItem(rs.getInt("replaced"));
				        }
				          
				        else if (rs.getInt("replaced") == 0) {
				        	//msg = msg + "created " + etc.getDataSource().getItem(rs.getInt("type"));
				        }
				          
				        else {
				        	//msg = msg + "replaced " + etc.getDataSource().getItem(rs.getInt("replaced")) + " with " + etc.getDataSource().getItem(rs.getInt("type"));
				        }  
				        this.player.sendMessage("§6" + msg);
				        hist = true;
				      }
				    } catch (SQLException ex) {
				     // log.log(Level.SEVERE, getClass().getName() + " SQL exception", ex);
				    } finally {
				      try {
				        if (rs != null)
				          rs.close();
				        if (ps != null)
				          ps.close();
				        if (this.conn != null)
				          this.conn.close();
				      } catch (SQLException ex) {
				       // log.log(Level.SEVERE, getClass().getName() + " SQL exception on close", ex);
				      }
				    }
				    if (!hist)
				      this.player.sendMessage("§3None.");
					
				}
				
			});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IResultObject areaStats(Vector origin, int size, int world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResultObject areaPlayerStats(Vector origin, int size, int world,
			PlayerWrapper player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResultObject worldBlockStats(int world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResultObject getBlockHistory(Vector v, int world, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processBlockList() {
		// TODO Auto-generated method stub
		
	}

	

}
