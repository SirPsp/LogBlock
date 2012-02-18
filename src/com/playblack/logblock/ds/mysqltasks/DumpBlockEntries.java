package com.playblack.logblock.ds.mysqltasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.ds.MysqlData;
import com.playblack.logblock.ds.services.ConnectionService;
import com.playblack.logblock.utils.BlockEntry;


public class DumpBlockEntries implements Runnable {

	private int limit;
	private ConnectionService cs;
	private Logger log;
	public DumpBlockEntries(Logger log, int limit, ConnectionService cs) {
		this.limit = limit;
		this.log = log;
		this.cs = cs;
		
	}
	@Override
	public void run() {
		PreparedStatement ps = null;
		Connection conn = null;
			int count = 0;

			if(MysqlData.blockList.isEmpty()) {
				return;
			}
			if (MysqlData.blockList.size() > limit) {
				log.info("LogBlock queue lenght exceeds query limit. Remaining blocks will be dumped in the next run!");
				log.info("Queue lenght: " + MysqlData.blockList.size() + " / Query Limit: " + limit);
			}
			try {
				conn = cs.getConnection();
				conn.setAutoCommit(false);
				while ((!MysqlData.blockList.isEmpty()) && (count < limit)) {
					BlockEntry b = (BlockEntry) MysqlData.blockList.poll(1L, TimeUnit.SECONDS);

					if (b == null) {
						continue;
					}
					ps = conn.prepareStatement(
									"INSERT INTO blocks (date, player, replaced, type, damage, x, y, z, world) VALUES (now(),?,?,?,?,?,?,?,?)",
									Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, b.player);
					ps.setInt(2, b.oldBlock.getType());
					ps.setInt(3, b.newBlock.getType());
					ps.setInt(4, b.oldBlock.getData());
					ps.setInt(5, b.position.getBlockX());
					ps.setInt(6, b.position.getBlockY());
					ps.setInt(7, b.position.getBlockZ());
					ps.setInt(8, b.newBlock.getWorld());
					ps.executeUpdate();					
					if (b.extra != null) {
						ResultSet keys = ps.getGeneratedKeys();
						int key=-1;
						if(keys.next()) {
							key = keys.getInt(1);
							System.out.println("Inserting block "+b.newBlock.getType()+" returned ID: "+key);
						}
						

						ps = conn
								.prepareStatement("INSERT INTO extra (id, extra) values (?,?)");
						ps.setInt(1, key);
						ps.setString(2, b.extra);
						ps.executeUpdate();
					}

					count++;
				}
				conn.commit();
			} 
			catch (InterruptedException ex) {
				log.log(Level.SEVERE, "LogBlock BlockDumper has been interrupted! (Restart or shutdown?)");
			} 
			catch (SQLException ex) {
				log.log(Level.SEVERE, "LogBlock SQL exception", ex);
			} 
			finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null) {
						if(!conn.isClosed()) {
							conn.close();
						}
					}
						
				} catch (SQLException ex) {
					log.log(Level.SEVERE, "LogBlock SQL exception on close", ex);
				}
			}
	}

}
