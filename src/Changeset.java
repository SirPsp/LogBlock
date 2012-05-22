import com.playblack.logblock.blocks.IBlock;
import com.playblack.mcutils.Vector;


public class Changeset {
    public Vector position;
    
    public IBlock currentBlock;
    
    public IBlock recordedBlock;
    
    public Changeset(Vector v, IBlock original, IBlock replaced) {
        position = v;
        currentBlock = original;
        recordedBlock = replaced;
    }
}
