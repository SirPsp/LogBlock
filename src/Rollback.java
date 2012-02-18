import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.blocks.IBlock;
import com.playblack.logblock.blocks.SignBlock;
import com.playblack.logblock.blocks.WorldBlock;
import com.playblack.mcutils.Vector;

/*
 * //TODO: Move into a package and wrap up
 */
public class Rollback implements Runnable {
	static final Logger log = Logger.getLogger("Minecraft");
	//private LinkedBlockingQueue<Rollback.Edit> edits = new LinkedBlockingQueue<Rollback.Edit>();
	private HashMap<Vector, IBlock> currentBlocks;
	private HashMap<Vector, IBlock> recordedBlocks;
	
	Object lock = new Object();

	Rollback(Connection conn, String name, int minutes) {
		if(conn == null) {
			throw new NullPointerException();
		}
		String query = "select type, replaced, damage, x, y, z, id, world from blocks where player = ? and date > date_sub(now(), interval ? minute) order by date desc";
		PreparedStatement ps = null;
		ResultSet rs = null;
		currentBlocks = new HashMap<Vector, IBlock>();
		recordedBlocks = new HashMap<Vector, IBlock>();

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(query, 1);
			ps.setString(1, name);
			ps.setInt(2, minutes);
			rs = ps.executeQuery();
			//Lock this up
			synchronized(lock) {
				while (rs.next()) {
					Vector p = new Vector(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
					int type  = rs.getInt("type");
					int world = rs.getInt("world");
					byte data = (byte)rs.getInt("damage");
					
					if(rs.getInt("replaced") == 63) {
						
						int bId = rs.getInt("id");
						
						//WorldBlock bOriginal = new WorldBlock(rs.getInt("type"), 0, rs.getInt("world"));
						
//						SignBlock bReplaced = new SignBlock();
//						bReplaced.setWorld(rs.getInt("world"));
//						bReplaced.setData((byte)rs.getInt("damage")); //heading
//						
//						bReplaced.setText(getSignContent(conn, bId));
						
						currentBlocks.put(p,
								new WorldBlock(type, 0, world));
						recordedBlocks.put(p,
								new SignBlock(63, data, 
										world, 
										getSignContent(conn, bId)));
					}
					else {			
//						WorldBlock bOriginal = new WorldBlock(rs.getInt("type"), 0, rs.getInt("world"));
//						
//						WorldBlock bReplaced = new WorldBlock(rs.getInt("replaced"),
//								rs.getInt("damage"), 
//								rs.getInt("world"));
						
						currentBlocks.put(p, 
								new WorldBlock(type, 0, world));
						recordedBlocks.put(p, 
								new WorldBlock(rs.getInt("replaced"),
										data, 
										world));
					}
					//TODO: add chests
				}
			}
			
		} catch (SQLException ex) {
			log.log(Level.SEVERE, getClass().getName() + " SQL exception", ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, getClass().getName()
						+ " SQL exception on close", ex);
			}
		}
	}

	public int count() {
//		System.out.println("CURRENT BLOCKS");
//		System.out.println(currentBlocks.toString());
//		System.out.println("RECORDED BLOCKS");
//		System.out.println(recordedBlocks.toString());
		return currentBlocks.size();
	}
	
	private ArrayList<String> getSignContent(Connection conn, int blockId) {
		ArrayList<String> ret = new ArrayList<String>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM extra WHERE id=?");
			ps.setInt(1, blockId);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				//split by those patterns: " [" or "] [" or "] " or "]"
				//This will fail if the sign content contains [ or ] by itself
				//however due to legacy support there's nothing to do against, yet
				String[] signText = rs.getString("extra").split("((\\s\\[|(\\]\\s\\[))|((\\]\\s))|\\])");
				if(!signText[0].equalsIgnoreCase("sign")) {
					return ret; //that wasn't a sign here
				}
				//Skip the first element as its is only an indicator what block this was
				for(int i = 1; i<signText.length; i++) {
					ret.add(signText[i]);
				}
				return ret;
			}
		} catch (SQLException e) {
			//juck ...
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Softly change a block in the world.<br>
	 * This preloads a chunk if it's not loaded.
	 * @param type
	 * @param data
	 * @param coords
	 * @param world
	 */
	private void changeBlock(IBlock block, Vector coords, World world) {
		preloadChunk(world, coords);
		Block b = world.getBlockAt(coords.getBlockX(), coords.getBlockY(), coords.getBlockZ());

        if(b.getType() == block.getType() && b.getData() == block.getData()) {
        	return;
        }
        if(b.getType() != block.getType()) {
        	world.setBlockAt(block.getType(), coords.getBlockX(), coords.getBlockY(), coords.getBlockZ());
        	if(b.getData() != block.getData()) {
        		world.setBlockData(coords.getBlockX(), coords.getBlockY(), coords.getBlockZ(), block.getData());
        	}
        	//After setting it all
        	if(block.getType() == 63) {
            	try {
            		Sign sign = (Sign)world.getComplexBlock(b);  	
                	sign.setText(0, ((SignBlock)block).getAtLine(0));
                	sign.setText(1, ((SignBlock)block).getAtLine(1));
                	sign.setText(2, ((SignBlock)block).getAtLine(2));
                	sign.setText(3, ((SignBlock)block).getAtLine(3));
                	sign.getBlock().setData((byte)((SignBlock)block).getData());
                	sign.update();
            	}
            	catch(ClassCastException e) {
            		
            	}
            }	
        }
        //TODO: Add chests
	}
	
	
	/**
	 * Preload a chunk.
	 * @param world 
	 * @param coord
	 */
	private void preloadChunk(World world, Vector coord) {
        if (!world.isChunkLoaded(coord.getBlockX(), coord.getBlockY(), coord.getBlockZ())) {
            world.loadChunk(coord.getBlockX(), coord.getBlockY(), coord.getBlockZ());
        }
    }
	
	/**
	 * Apply a set of changes to the world
	 * @param player
	 * @param selection
	 */
	public boolean modifyWorld() {
		if(currentBlocks.isEmpty() || recordedBlocks.isEmpty()) {
			//nothign there, return
			return false;
		}	
		synchronized (lock) {
			World world = null;
			for(Vector v : currentBlocks.keySet()) {
				if(world == null) {
					world = etc.getServer().getWorld(currentBlocks.get(v).getWorld());
				}
				else if(!world.equals(etc.getServer().getWorld(currentBlocks.get(v).getWorld()))) {
					world = (etc.getServer().getWorld(currentBlocks.get(v).getWorld()));
				}
				Block b = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
				if(b.getType() == currentBlocks.get(v).getType()) {
					changeBlock(recordedBlocks.get(v), v, world);
				}
			}
			return true;
		}
	}

	/**
	 * Check the block lists for consistency. If they are not the sme size,
	 * there's somethign really foul going on which might result in crazy rollbacks
	 * @return
	 */
	private boolean checkConsistency() {
		if(currentBlocks.size() == recordedBlocks.size()) {
			return true;
		}
		return false;
	}
	
	public void run() {
		if(checkConsistency()) {
			modifyWorld();
		}
	}
}