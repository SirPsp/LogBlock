import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playblack.logblock.blocks.SignBlock;
import com.playblack.logblock.blocks.WorldBlock;
import com.playblack.mcutils.Vector;

/*
 * //TODO: Move into a package and wrap up
 */
public class Rollback implements Runnable {
    static final Logger log = Logger.getLogger("Minecraft");
    private LinkedBlockingQueue<Changeset> changesets = new LinkedBlockingQueue<Changeset>();

    Object lock = new Object();

    Rollback(Connection conn, String name, int minutes) {
        if (conn == null) {
            throw new NullPointerException();
        }
        String query = "select type, replaced, damage, x, y, z, id, dimension, world from blocks where player = ? and date > date_sub(now(), interval ? minute) order by date desc";
        PreparedStatement ps = null;
        ResultSet rs = null;
        changesets = new LinkedBlockingQueue<Changeset>();

        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(query, 1);
            ps.setString(1, name);
            ps.setInt(2, minutes);
            rs = ps.executeQuery();
            // Lock this up
            synchronized (lock) {
                while (rs.next()) {
                    Vector p = new Vector(rs.getInt("x"), rs.getInt("y"),
                            rs.getInt("z"));
                    int type = rs.getInt("type");
                    int dimension = rs.getInt("dimension");
                    String world = rs.getString("world");
                    byte data = (byte) rs.getInt("damage");
                    int replaced = rs.getInt("replaced");
                    if (replaced == 63) {

                        int bId = rs.getInt("id");
                        changesets.add( 
                                new Changeset(
                                        p, 
                                        new WorldBlock(type,0,dimension,world), 
                                        new SignBlock(63, data, dimension, world, getSignContent(conn, bId))));
//                        currentBlocks.put(p, new WorldBlock(type, 0, world));
//                        recordedBlocks.put(p, new SignBlock(63, data, world,
//                                getSignContent(conn, bId)));
                    } else {
                        changesets.add( 
                                new Changeset(
                                        p, 
                                        new WorldBlock(type,0,dimension,world), 
                                        new WorldBlock(replaced, data, dimension, world)));
//                        currentBlocks.put(p, new WorldBlock(type, 0, world));
//                        recordedBlocks.put(p,
//                                new WorldBlock(replaced, data,
//                                        world));
                    }
                    // TODO: add chests
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
        return changesets.size();
    }

    private ArrayList<String> getSignContent(Connection conn, int blockId) {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * FROM extra WHERE id=?");
            ps.setInt(1, blockId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // split by those patterns: " [" or "] [" or "] " or "]"
                // This will fail if the sign content contains [ or ] by itself
                String[] signText = rs.getString("extra").split(
                        "((\\s\\[|(\\]\\s\\[))|((\\]\\s))|\\])");
                if (!signText[0].equalsIgnoreCase("sign")) {
                    return ret; // that wasn't a sign here
                }
                // Skip the first element as its is only an indicator what block
                // this was
                for (int i = 1; i < signText.length; i++) {
                    ret.add(signText[i]);
                }
                return ret;
            }
        } catch (SQLException e) {
            // juck ...
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Softly change a block in the world.<br>
     * This preloads a chunk if it's not loaded.
     * 
     * @param type
     * @param data
     * @param coords
     * @param world
     */
    private void changeBlock(Changeset set, World world) {
        preloadChunk(world, set.position);
        Block b = world.getBlockAt(set.position.getBlockX(), set.position.getBlockY(),  set.position.getBlockZ());
        if (b.getType() == set.recordedBlock.getType() && b.getData() == set.recordedBlock.getData()) {
            return;
        }
        world.setBlockAt(set.recordedBlock.getType(), set.position.getBlockX(), set.position.getBlockY(), set.position.getBlockZ());
        if (b.getData() != set.recordedBlock.getData()) {
            world.setBlockData(set.position.getBlockX(), set.position.getBlockY(),
                    set.position.getBlockZ(), set.recordedBlock.getData());
        }
        // After setting it all
        if (set.recordedBlock.getType() == 63) {
            try {
                Sign sign = (Sign) world.getComplexBlock(b);
                sign.setText(0, ((SignBlock) set.recordedBlock).getAtLine(0));
                sign.setText(1, ((SignBlock) set.recordedBlock).getAtLine(1));
                sign.setText(2, ((SignBlock) set.recordedBlock).getAtLine(2));
                sign.setText(3, ((SignBlock) set.recordedBlock).getAtLine(3));
                sign.getBlock().setData(
                        (byte) ((SignBlock) set.recordedBlock).getData());
                sign.update();
            } catch (ClassCastException e) {

            }
        }
        // TODO: Add chests
    }

    /**
     * Preload a chunk.
     * 
     * @param world
     * @param coord
     */
    private void preloadChunk(World world, Vector coord) {
        if (!world.isChunkLoaded(coord.getBlockX(), coord.getBlockY(),
                coord.getBlockZ())) {
            world.loadChunk(coord.getBlockX(), coord.getBlockY(),
                    coord.getBlockZ());
        }
    }

    /**
     * Apply a set of changes to the world
     * 
     * @param player
     * @param selection
     */
    public boolean modifyWorld() {
        if (changesets.isEmpty()) {
            // nothign there, return
            return false;
        }
        synchronized (lock) {
            World world = null;
            Changeset changeset = changesets.poll();
            while (changeset != null)
            {
                world = etc.getServer().getWorld(changeset.currentBlock.getWorld())[changeset.currentBlock.getDimension()];
                log.info(changeset.currentBlock.getWorld());
                changeBlock(changeset, world);
                changeset = changesets.poll();
            }
            return true;
        }
    }

    public void run() {
        modifyWorld();
    }
}