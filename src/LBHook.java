import java.util.ArrayList;

import com.playblack.logblock.blocks.*;
import com.playblack.logblock.ds.IDataSource;
import com.playblack.mcutils.Vector;


public class LBHook implements PluginInterface {

	IDataSource ds;
	public LBHook(IDataSource ds) {
		this.ds = ds;
	}
	@Override
	public String checkParameters(Object[] ob) {
		if(ob.length != getNumParameters()) {
			return "LogBlockAPI: Paramater count not matching!";
		}
		return null;
	}

	@Override
	public String getName() {
		return "LogBlockAPI";
	}

	@Override
	public int getNumParameters() {
		return 3;
	}

	private int getWorldId(World world) {
		return world.getType().getId();

	}
	
	private ArrayList<String> processSignText(Block b) {
		Sign s = (Sign)b.getWorld().getComplexBlock(b);
		ArrayList<String> txt = new ArrayList<String>();
		for(int i=0; i < 4; i++) {
			txt.add(s.getText(i));
		}
		return txt;
	}
	private void queue(String player, Block oldBlock, Block newBlock) {
		Vector v = null;
		IBlock oBlock = null;
		IBlock nBlock = null;
		if(oldBlock != null) {
			v = new Vector(oldBlock.getX(), oldBlock.getY(), oldBlock.getZ());
			
			if(oldBlock.getType() == 63) {
				oBlock = new SignBlock(oldBlock.getType(), 
						(byte)oldBlock.getData(), 
						getWorldId(oldBlock.getWorld()),
						processSignText(oldBlock));
			}
			else {
				oBlock = new WorldBlock(oldBlock.getType(), 
						(byte)oldBlock.getData(), 
						getWorldId(oldBlock.getWorld()));
			}
		}
		if(newBlock != null) {
			if(v == null) {
				v = new Vector(newBlock.getX(), newBlock.getY(), newBlock.getZ());
			}
			if(newBlock.getType() == 63) {
				nBlock = new SignBlock(newBlock.getType(), 
						(byte)newBlock.getData(), 
						getWorldId(newBlock.getWorld()),
						processSignText(newBlock));
			}
			else {
				nBlock = new WorldBlock(newBlock.getType(), 
						(byte)newBlock.getData(), 
						getWorldId(newBlock.getWorld()));
			}
		}
		ds.queueBlock(player, oBlock, nBlock, v);
	}
	
	@Override
	public Object run(Object[] ob) {
		//Firstly, get the playername
		String player;
		if(ob[0] instanceof String) {
			player = (String)ob[0];
		}
		else {
			return null;
		}
		Block oldBlock;
		Block newBlock;
		//Process oldBlock
		if((ob[1] != null) && ob[1] instanceof Block) {
			oldBlock = (Block)ob[1];
		}
		else {
			oldBlock = null;
		}
		
		//process newBlock
		if((ob[2] != null) && ob[2] instanceof Block) {
			newBlock = (Block)ob[2];
		}
		else {
			newBlock = null;
		}
		
		queue(player, oldBlock, newBlock);
		return null;
	}

}
