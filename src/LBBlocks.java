import java.util.ArrayList;
import java.util.logging.Logger;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.blocks.SignBlock;
import com.playblack.logblock.blocks.WorldBlock;
import com.playblack.logblock.ds.IDataSource;
import com.playblack.logblock.utils.LogBlockConfig;
import com.playblack.mcutils.PlayerWrapper;
import com.playblack.mcutils.Vector;


public class LBBlocks extends PluginListener {
	IDataSource taskManager;
	IBlock lastface = null;
	LogBlockConfig cfg;
	
	public LBBlocks(IDataSource ds, LogBlockConfig cfg) {
		this.taskManager = ds;
		this.cfg = cfg;
	}
	
	/**
	 * Get integer from world id
	 * @param world
	 * @return
	 */
//	private int getDimensionId(World world) {
//		return world.getType().getId();
////		if(world.getType() == World.Type.NORMAL) {
////			return 0;
////		}
////		else if(world.getType() == World.Type.NETHER) {
////			return -1;
////		}
////		else {
////			return 1;
////		}
//	}
	
	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		if ((itemInHand.getItemId() == cfg.getBlockToolId())
				&& (player.canUseCommand("/blockhistory"))) {
			taskManager.getBlockHistory(new PlayerWrapper(player, Logger.getLogger("Minecraft")), 
					new Vector(blockPlaced.getX(), blockPlaced.getY(), blockPlaced.getZ()), 
					blockPlaced.getWorld().getType().getId(), blockPlaced.getWorld().getName().replace("worlds/", ""), 
					cfg.getQueryLimit());
			//LogBlock.this.showBlockHistory(player, blockPlaced);
			//return LogBlock.this.toolblockRemove;
			return cfg.removeToolBlock();
		}

		if (cfg.isDebug()) {
			Logger.getLogger("Minecraft").info("onBlockPlace: placed "
					+ blockPlaced.getType() + " clicked "
					+ blockClicked.getType() + " item "
					+ itemInHand.getItemId());
		}
		IBlock bPlaced;
		if(etc.getDataSource().getItem(blockPlaced.getType()).equalsIgnoreCase("sign") ) {
			bPlaced = new SignBlock();
			bPlaced.setDimension(blockPlaced.getWorld().getType().getId());
			bPlaced.setWorld(blockPlaced.getWorld().getName().replace("worlds/", ""));
			Sign s = (Sign)blockPlaced.getWorld().getComplexBlock(blockPlaced);
			ArrayList<String> txt = new ArrayList<String>();
			for(int i=0; i < 4; i++) {
				txt.add(s.getText(i));
			}
			((SignBlock)bPlaced).setText(txt);
			
		}
		else {
			bPlaced = new WorldBlock(blockPlaced.getType(), 
					blockPlaced.getData(), 
					blockPlaced.getWorld().getType().getId(),
					blockPlaced.getWorld().getName().replace("worlds/", ""));
		}
				
		Vector v = new Vector(blockPlaced.getX(),
				blockPlaced.getY(),
				blockPlaced.getZ());
		
		taskManager.queueBlock(player.getName(), lastface, bPlaced, v, player.getWorld().getName().replace("worlds/", ""));		
//		LogBlock.this.queueBlock(player, LogBlock.this.lastface,
//				blockPlaced);
		return false;
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		IBlock b;
		if((block.getType() == 63) ||  (block.getType() == 323)) {
			b = new SignBlock();
			b.setData((byte)block.getData());
			b.setType(63);
			b.setDimension(block.getWorld().getType().getId());
			b.setWorld(block.getWorld().getName().replace("worlds/", ""));
			Sign s = (Sign)block.getWorld().getComplexBlock(block);
			ArrayList<String> txt = new ArrayList<String>();
			for(int i=0; i < 4; i++) {
				txt.add(s.getText(i));
			}
			((SignBlock)b).setText(txt);
		}
		else {
			b = new WorldBlock(block.getType(), 
					block.getData(), 
					block.getWorld().getType().getId(),
					block.getWorld().getName().replace("worlds/", ""));
		}
		Vector v = new Vector(block.getX(),
				block.getY(),
				block.getZ());
		taskManager.queueBlock(player.getName(), b, null, v, player.getWorld().getName().replace("worlds/", ""));
		//queueBlock(player, block, null);
		return false;
	}
	
	@Override
	public boolean onSignChange(Player player, Sign sign) {
		SignBlock b = new SignBlock();
		b.setDimension(sign.getWorld().getType().getId());
		ArrayList<String> txt = new ArrayList<String>();
		for(int i=0; i < 4; i++) {
			txt.add(sign.getText(i));
		}
		b.setText(txt);
		Vector v = new Vector(sign.getX(),
				sign.getY(),
				sign.getZ());
		taskManager.queueBlock(player.getName(), null, b, v, player.getWorld().getName().replace("worlds/", ""));
		
		//lock.this.queueSign(player, sign);
		return false;
	}
	@Override
	public boolean onBlockRightClick(Player player, Block blockClicked, Item item) {
		if (item.getItemId() == cfg.getToolId() && player.canUseCommand("/blockhistory")) {
			taskManager.getBlockHistory(new PlayerWrapper(player, Logger.getLogger("Minecraft")), 
					new Vector(blockClicked.getX(), blockClicked.getY(), blockClicked.getZ()), 
					blockClicked.getWorld().getType().getId(), 
					blockClicked.getWorld().getName().replace("worlds/", ""),
					cfg.getQueryLimit());
			//showBlockHistory(player, blockClicked);
			return true;
		}

		Block facing = blockClicked.getFace(blockClicked.getFaceClicked());
		lastface = new WorldBlock(facing.getType(), facing.getData(), facing.getWorld().getType().getId(), facing.getWorld().getName().replace("worlds/", ""));
		
		if (cfg.isDebug()) {
			Logger.getLogger("Minecraft").info("onBlockRightClick: clicked "
					+ blockClicked.getType()
					+ " item "
					+ item.getItemId()
					+ " face "
					+ blockClicked.getFace(blockClicked.getFaceClicked())
							.getType());
			return true;
		}
		return false;
	}

	@Override
	public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
		if ((item.getItemId() != 326) && (item.getItemId() != 327)) {
			return false;
		}
		if(blockPlaced != null) {
			IBlock b = new WorldBlock(blockPlaced.getType(), 
					blockPlaced.getData(), 
					blockPlaced.getWorld().getType().getId(),
					blockPlaced.getWorld().getName().replace("worlds/", ""));
			Vector v = new Vector(blockPlaced.getX(), blockPlaced.getY(), blockPlaced.getZ());
			taskManager.queueBlock(player.getName(), b, lastface, v, player.getWorld().getName().replace("worlds/", ""));
		}
		if(blockClicked != null) {
			IBlock b = new WorldBlock(blockClicked.getType(), 
					blockClicked.getData(), 
					blockClicked.getWorld().getType().getId(),
					blockClicked.getWorld().getName().replace("worlds/", ""));
			Vector v = new Vector(blockClicked.getX(), blockClicked.getY(), blockClicked.getZ());
			taskManager.queueBlock(player.getName(), b, lastface, v, player.getWorld().getName().replace("worlds/", ""));
		}
		if (cfg.isDebug()) {
			Logger.getLogger("Minecraft").info("onItemUse: placed "
					+ blockPlaced.getType() + " clicked "
					+ blockClicked.getType() + " item " + item.getItemId());
		}
		return false;
	}
}
