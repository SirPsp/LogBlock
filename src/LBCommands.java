import java.util.logging.Logger;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.blocks.WorldBlock;
import com.playblack.logblock.ds.IDataSource;
import com.playblack.logblock.utils.LogBlockConfig;
import com.playblack.mcutils.PlayerWrapper;


public class LBCommands extends PluginListener {
	IDataSource taskManager;
	LogBlockConfig cfg;
	Logger log = Logger.getLogger("Minecraft");
	public LBCommands(IDataSource manager, LogBlockConfig cfg) {
		taskManager = manager;
		this.cfg = cfg;
	}
	
	/**
	 * Take a string and parse an amount of minutes.
	 * A String should be formatted like this: number minutes|hours|days|months
	 * Ex: 1 month and it will return the amount of minutes that contain one month
	 * @param ts
	 * @return
	 */
	private int parseTimeSpec(String ts) {
		String[] split = ts.split(" ");

		if (split.length < 2)
			return 0;
		int min;
		try {
			min = Integer.parseInt(split[0]);
		} catch (NumberFormatException ex) {
			return 0;
		}

		if (split[1].startsWith("hour"))
			min *= 60;
		else if (split[1].startsWith("day")) {
			min *= 1440;
		}
		else if(split[1].startsWith("month")) {
			min *= 43200;
		}
		return min;
	}
	
	/**
	 * Get integer from world id
	 * @param world
	 * @return
	 */

	public boolean onCommand(Player player, String[] split) {
		if (!player.canUseCommand(split[0])) {
			return false;
		}
		
		if (split[0].equalsIgnoreCase("/lb")) {
			if (split.length == 1) {
//				AreaStats th = new AreaStats(conn, player,
//						LogBlock.this.defaultDist);
//				new Thread(th).start();
				taskManager.areaStats(new PlayerWrapper(player, log), cfg.getDefaultDistance(), player.getWorld().getType().getId(), player.getWorld().getName().replace("worlds/", ""));
				return true;
			}

			if (split.length == 2) {
				if (split[1].equalsIgnoreCase("world")) {
//					PlayerWorldStats th = new PlayerWorldStats(conn, player);
//					new Thread(th).start();
					taskManager.worldBlockStats(player.getWorld().getType().getId(), 
							player.getWorld().getName().replace("worlds/", ""), 
							new PlayerWrapper(player, log));
					return true;
				}
				player.notify("Incorrect usage.");
				return true;
			}

			if (split[1].equalsIgnoreCase("player")) {
//				PlayerAreaStats th = new PlayerAreaStats(conn, player,
//						split[2], LogBlock.this.defaultDist);
//				new Thread(th).start();
				if(split.length >= 3) {
					taskManager.areaPlayerStats(cfg.getDefaultDistance(), 
							player.getWorld().getType().getId(),
							player.getWorld().getName().replace("worlds/", ""), 
							new PlayerWrapper(player, log), 
							split[2]);
				}
				else {
					player.notify("Incorrect usage.");
				}
				return true;
				
			}

			if (split[1].equalsIgnoreCase("area")) {
//				AreaStats th = new AreaStats(conn, player,
//						Integer.parseInt(split[2]));
//				new Thread(th).start();
				if(split.length >= 3) {
					taskManager.areaStats(new PlayerWrapper(player, log), 
							Integer.parseInt(split[2]), 
							player.getWorld().getType().getId(), 
							player.getWorld().getName().replace("worlds/", ""));
				}
				else {
					player.notify("Incorrect usage.");
				}
				return true;
			}

			if (split[1].equalsIgnoreCase("block")) {
//				int type = etc.getDataSource().getItem(split[2]);
//				AreaBlockSearch th = new AreaBlockSearch(conn, player,
//						type, LogBlock.this.defaultDist);
//				new Thread(th).start();
				if(split.length >= 3) {
					IBlock b = new WorldBlock(0,0,0, player.getWorld().getName().replace("worlds/", ""));
					b.setType(Integer.parseInt(split[2]));
					taskManager.areaBlockSearch(new PlayerWrapper(player, log), 
							cfg.getDefaultDistance(), 
							player.getWorld().getType().getId(), 
							player.getWorld().getName().replace("worlds/", ""), 
							b);
					return true;
				}
				player.notify("Incorrect usage.");
				return true;
			}
		}

		if (split[0].equalsIgnoreCase("/rollback")) {
			if (split.length < 3) {
				player.notify("Usase: /rollback [player] [## minutes|hours|days|months]");
				return true;
			}
			String name = split[1];
			int minutes = parseTimeSpec(etc.combineSplit(2,
					split, " "));
			if (minutes <= 0) {
				player.notify("Usage: /rollback [player] [## minutes|hours|days|months]");
				return true;
			}
			player.sendMessage(Colors.Gray+"Rolling back " + name + " by " + minutes
					+ " minutes.");
//			Connection conn;
//			try {
//				conn = LogBlock.this.getConnection();
//			} catch (SQLException ex) {
//				LogBlock.log.log(Level.SEVERE, name + " SQL exception", ex);
//				player.notify("Error, check server logs.");
//				return true;
//			}
			Rollback rb;
			try {
				rb = new Rollback(taskManager.getConnection(), name, minutes);
			}
			catch(NullPointerException e) {
				log.warning(e.getMessage()); //rollback throws it when connection is null
				player.notify(e.getMessage());
				return true;
			}

			player.sendMessage(Colors.LightGreen+"Edit count: " + rb.count());

			taskManager.executeOther(rb);
			return true;
		}
		
		if (split[0].equalsIgnoreCase("/lb_cleanup")) {
			int minutes=0;
			if(split.length < 3) {
				player.notify("Usage: /lb_cleanup ## minute/hour/day/month - where ## is the time amount ;)");
			}
			if(split.length >= 3) {
				minutes = parseTimeSpec(etc.combineSplit(1, split, " "));
			}
			if (minutes <= 0) {
				player.notify("WARNING: You defined a faulty thing for the cleanup. Syntax is ## minute/hour/day/month");
				return true;
			}
			
			Maintainance rb;
			try {
				rb = new Maintainance(taskManager.getConnection(), minutes);
			}
			catch(NullPointerException e) {
				log.warning(e.getMessage()); //rollback throws it when connection is null
				player.notify(e.getMessage());
				return true;
			}
			taskManager.executeOther(rb);
			player.sendMessage(Colors.LightGreen+"LogBlock history cleaned up!");
			return true;
		}

		return false;
	}
}
