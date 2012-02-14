import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;


//TODO: Move into a package and wrap up
public class Maintainance implements Runnable {

	String query;
	Connection conn;
	int minutes;
	public Maintainance(Connection conn, int minutes) {
		query="DELETE FROM blocks WHERE date < date_sub(now(), interval ? minute)";
		this.conn = conn;
		this.minutes = minutes;
	}
	@Override
	public void run() {
		try {
			PreparedStatement ps;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(query);
			ps.setInt(1, minutes);
			ps.executeUpdate();
			
		} catch (SQLException ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, getClass().getName() + " SQL exception", ex);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				Logger.getLogger("Minecraft").log(Level.SEVERE, getClass().getName()
						+ " SQL exception on close", ex);
			}
		}
		
	}

}
