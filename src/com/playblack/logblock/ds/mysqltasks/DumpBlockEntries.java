package com.playblack.logblock.ds.mysqltasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.ds.MysqlData;
import com.playblack.logblock.utils.BlockEntry;


public class DumpBlockEntries implements Runnable {

	private int limit;
	private int delay;
	private Connection conn;
	private Logger log;
	public DumpBlockEntries(Connection conn, Logger log, int delay, int limit) {
		this.limit = limit;
		this.delay = delay;
		this.conn = conn;
		this.log = log;
		
	}
	@Override
	public void run() {
		PreparedStatement ps = null;
		while (true) { //TODO: find a better method to do this
			long start = System.currentTimeMillis() / 1000L;
			int count = 0;

			if (MysqlData.blockList.size() > limit) {
				log.info("LogBlock queue size " + MysqlData.blockList.size() + "(No, I don't know why you need to know)");
			}

			try {
				conn.setAutoCommit(false);
				while ((count < limit) && (start + this.delay > System.currentTimeMillis() / 1000L)) {
					BlockEntry b = (BlockEntry) MysqlData.blockList.poll(1L, TimeUnit.SECONDS);

					if (b == null) {
						continue;
					}
					ps = conn.prepareStatement(
									"INSERT INTO blocks (date, player, replaced, type, damage, x, y, z, world) VALUES (now(),?,?,?,?,?,?,?,?)",
									1);
					ps.setString(1, b.player);
					ps.setInt(2, b.oldBlock.getType());
					ps.setInt(3, b.newBlock.getType());
					ps.setInt(4, b.newBlock.getData());
					ps.setInt(5, b.position.getBlockX());
					ps.setInt(6, b.position.getBlockY());
					ps.setInt(7, b.position.getBlockZ());
					ps.setInt(8, b.newBlock.getWorld());
					ps.executeUpdate();

					if (b.extra != null) {
						ResultSet keys = ps.getGeneratedKeys();
						keys.next();
						int key = keys.getInt(1);

						ps = conn
								.prepareStatement("INSERT INTO extra (id, extra) values (?,?)");
						ps.setInt(1, key);
						ps.setString(2, b.extra);
						ps.executeUpdate();
					}

					count++;
				}
//				if ((LogBlock.this.debug) && (count > 0))
//					LogBlock.lblog.info("Commiting " + count + " inserts.");
				conn.commit();
			} 
			catch (InterruptedException ex) {
				log.log(Level.SEVERE, "LogBlock interrupted exception", ex);
			} 
			catch (SQLException ex) {
				log.log(Level.SEVERE, "LogBlock SQL exception", ex);
			} 
			finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					log.log(Level.SEVERE, "LogBlock SQL exception on close", ex);
				}
			}
		}

	}

}
