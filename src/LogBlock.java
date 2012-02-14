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
	LBCommands commands;
	IDataSource manager;
	
	@Override
	public void disable() {
		// TODO Auto-generated method stub

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
		if(cfg.useCanaryDb()) {
			manager = new MysqlData(connections, itemManager, Logger.getLogger("Minecraft"));
		}
		commands = new LBCommands(manager, cfg);

	}
	
	private void propsToConfig() {
		PropertiesFile props = new PropertiesFile("plugins/LogBlock/logblock.properties");
		cfg.setToolId(props.getInt("tool-id"));
		cfg.setDebug(props.getBoolean("debug"));
		cfg.setUseCanaryDb(props.getBoolean("use-hmod-db"));
		cfg.setDelay(props.getInt("delay"));
		cfg.setRemoveBlockToolBlock(props.getInt("tool-block-remove"));
		cfg.setBlockToolId(props.getInt("tool-block-id"));
		cfg.setDefaultDistance(props.getInt("default-distance"));
		
		cfg.setDbPassword(props.getString("password"));
		cfg.setDbUsername(props.getString("username"));
		cfg.setDbUrl(props.getString("url"));
		cfg.setDbDriver(props.getString("driver"));
	}

}
