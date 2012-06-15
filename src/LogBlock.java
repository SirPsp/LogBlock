//import java.util.HashMap;
import java.io.File;
import java.util.logging.Logger;

//import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.ds.IDataSource;
import com.playblack.logblock.ds.MysqlData;
import com.playblack.logblock.ds.services.ConnectionService;
import com.playblack.logblock.utils.CanaryConnectionWrapper;
import com.playblack.logblock.utils.LogBlockConfig;
import com.playblack.mcutils.ItemManager;
//import com.playblack.mcutils.Vector;


public class LogBlock extends Plugin {

	private LogBlockConfig cfg;
	private ItemManager itemManager = new ItemManager(etc.getDataSource().getItems());
	private ConnectionService connections;
	private LBCommands commands;
	private LBBlocks blocks;
	public IDataSource manager;
	//public static HashMap<String,HashMap<Vector,IBlock>> undoLists = new HashMap<String,HashMap<Vector,IBlock>>();
	
	@Override
	public void disable() {
		manager.destroy();
		Logger.getLogger("Minecraft").info("LogBlock disabled. (Version 21)");
	}

	@Override
	public void enable() {
		propsToConfig();
		if(cfg.useCanaryDb()) {
			connections = new ConnectionService(new CanaryConnectionWrapper(etc.getInstance()));
		}
		else {
			connections = new ConnectionService(cfg.getDbUrl(), cfg.getDbUsername(), cfg.getDbPassword());
		}
		manager = new MysqlData(connections, itemManager, Logger.getLogger("Minecraft"));
		
		manager.startBlockDumper(cfg.getDelay(), cfg.getQueryLimit());
		
		commands = new LBCommands(manager, cfg); 
		blocks = new LBBlocks(manager, cfg);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, commands, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, blocks, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, blocks, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, blocks, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, blocks, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, blocks, this, PluginListener.Priority.LOW);
		//etc.getLoader().addListener(PluginLoader.Hook.EXPLODE, blocks, this, PluginListener.Priority.LOW);
		etc.getInstance().addCommand("/lb", " - LogBlock display command.");
		etc.getInstance().addCommand("/lb_cleanup", " - LogBlock table row cleaning.");
		etc.getInstance().addCommand("/rollback",
				" - LogBlock Rollback command.");
		etc.getLoader().addCustomListener(new LBHook(manager));
		Logger.getLogger("Minecraft").info("LogBlock enabled. (Version 21)");

	}
	
	public String getName() {
		return "LogBlock";
	}
	private void propsToConfig() {
		new File("plugins/LogBlock/").mkdir();
		PropertiesFile props = new PropertiesFile("plugins/LogBlock/logblock.properties");
		cfg = new LogBlockConfig();
		cfg.setToolId(props.getInt("tool-id", 270));
		cfg.setDebug(props.getBoolean("debug", false));
		cfg.setUseCanaryDb(props.getBoolean("use-canary-db", false)); //false default in case someone misses to rewrite that
		cfg.setDelay(props.getInt("delay", 10));
		cfg.setRemoveToolBlock(props.getBoolean("tool-block-remove", true));
		cfg.setBlockToolId(props.getInt("tool-block-id", 95));
		cfg.setDefaultDistance(props.getInt("default-distance", 20));
		
		cfg.setDbPassword(props.getString("password", "pass"));
		cfg.setDbUsername(props.getString("username", "root"));
		cfg.setDbUrl(props.getString("url","jdbc:mysql://localhost:3306/minecraft"));
		cfg.setDbDriver(props.getString("driver", "com.mysql.jdbc.Driver"));
		
		cfg.setQueryLimit(props.getInt("query-limit", 200));
		LogBlockConfig.setDateFormat(props.getString("date-format", "MM-dd hh:mm:ss"));
	}

}
