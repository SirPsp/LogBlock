import java.util.logging.Logger;

import com.playblack.logblock.ds.IDataSource;
import com.playblack.logblock.ds.MysqlData;
import com.playblack.logblock.ds.services.ConnectionService;
import com.playblack.logblock.utils.CanaryConnectionWrapper;
import com.playblack.logblock.utils.LogBlockConfig;
import com.playblack.mcutils.ItemManager;


public class LogBlock extends Plugin {

	private LogBlockConfig cfg;
	private ItemManager itemManager = new ItemManager(etc.getDataSource().getItems());
	private ConnectionService connections;
	private LBCommands commands;
	private LBBlocks blocks;
	public IDataSource manager;
	
	@Override
	public void disable() {
		manager.destroy();
		Logger.getLogger("Minecraft").info("LogBlock disabled. (Version 20)");
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
		
		manager.startBlockDumper(cfg.getDelay(), 100);
		
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
		Logger.getLogger("Minecraft").info("LogBlock enabled. (Version 20)");

	}
	
	public String getName() {
		return "LogBlock";
	}
	private void propsToConfig() {
		PropertiesFile props = new PropertiesFile("plugins/LogBlock/logblock.properties");
		cfg = new LogBlockConfig();
		cfg.setToolId(props.getInt("tool-id"));
		cfg.setDebug(props.getBoolean("debug"));
		cfg.setUseCanaryDb(props.getBoolean("use-canary-db", false)); //false default in case someone misses to rewrite that
		cfg.setDelay(props.getInt("delay"));
		cfg.setRemoveToolBlock(props.getBoolean("tool-block-remove"));
		cfg.setBlockToolId(props.getInt("tool-block-id"));
		cfg.setDefaultDistance(props.getInt("default-distance"));
		
		cfg.setDbPassword(props.getString("password"));
		cfg.setDbUsername(props.getString("username"));
		cfg.setDbUrl(props.getString("url"));
		cfg.setDbDriver(props.getString("driver"));
	}

}
