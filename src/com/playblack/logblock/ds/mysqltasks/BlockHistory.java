package com.playblack.logblock.ds.mysqltasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.ds.ExecutionTask;
import com.playblack.logblock.utils.LogBlockConfig;
import com.playblack.mcutils.ColorManager;
import com.playblack.mcutils.ItemManager;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;

public class BlockHistory extends ExecutionTask {

	public BlockHistory(PlayerWrapper p, Connection conn, Logger log, int world, Vector v, ItemManager im) {
		this.player = p;
		this.conn = conn;
		this.location = v;
		this.log = log;
		this.world = world;
		this.itemManager = im;
	}
	@Override
	public void run() {
		player.sendMessage(ColorManager.LightBlue + "Block history (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + "): ");
		boolean hist = false;
		PreparedStatement ps = null;
		ResultSet rs = null;

		SimpleDateFormat formatter = new SimpleDateFormat(LogBlockConfig.getDateFormat());
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("SELECT * from blocks left join extra using (id) where y = ? and x = ? and z = ?  and world = ? order by date desc limit 10", 1);
			ps.setInt(1, location.getBlockY());
			ps.setInt(2, location.getBlockX());
			ps.setInt(3, location.getBlockZ());
			ps.setInt(4, this.world);
			rs = ps.executeQuery();
			while (rs.next()) {
				Timestamp date = rs.getTimestamp("date");
				String datestr = formatter.format(date);
				
				StringBuilder sb = new StringBuilder();
				sb.append(datestr).append(" ").append(rs.getString("player")).append(" ");
				//String msg = datestr + " " + rs.getString("player") + " ";
				if (rs.getInt("type") == 0)
					sb.append("destroyed ")
					.append(itemManager.getItemName(rs.getInt("replaced")));
//					msg = msg
//							+ "destroyed "
//							+ itemManager.getItemName(rs.getInt("replaced"));
				else if (rs.getInt("replaced") == 0) {
					if (rs.getInt("type") == 323)
						sb.append("created ").append(rs.getString("extra"));
						//msg = msg + "created " + rs.getString("extra");
					else
						sb.append("created ")
						.append(itemManager.getItemName(rs.getInt("type")));
//						msg = msg
//								+ "created "
//								+ itemManager.getItemName(rs.getInt("type"));
				} else
					sb.append("replaced ")
					.append(itemManager.getItemName(rs.getInt("replaced"))).append(" with ")
					.append(itemManager.getItemName(rs.getInt("type")));

				
				player.sendMessage(ColorManager.prependColor(sb, ColorManager.Gold).toString());
				hist = true;
			}
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "LogBlock SQL exception", ex);
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
				log.log(Level.SEVERE, "LogBlock SQL exception on close", ex);
			}
		}
		if (!hist)
			player.sendMessage(ColorManager.LightBlue + "None.");

	}

}
