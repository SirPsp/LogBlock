package com.playblack.logblock.ds.services;

/*
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.playblack.logblock.utils.CanaryConnectionWrapper;

/**
 * Disclaimer:
 * Code is taken out of the JDBC Pool plugin package by bootswithdefer aka Jesse DeFer.
 * Find it here: https://github.com/bootswithdefer/boots-plugins/tree/master/JDBCPool
 */
public class ConnectionService {
  private Vector<JDCConnection> connections;
  private String url, user, password;
  final private long timeout = 60000;
  private ConnectionReaper reaper;
  final private int poolsize = 10;
  private CanaryConnectionWrapper ccw = null;

  /**
   * Native connection pool constructor
   * @param url
   * @param user
   * @param password
   */
  public ConnectionService(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
    connections = new Vector<JDCConnection>(poolsize);
    reaper = new ConnectionReaper(this);
    reaper.start();
  }
  
  /**
   * Canarymod connection constructor
   * @param ccw
   */
  public ConnectionService(CanaryConnectionWrapper ccw) {
	  this.ccw = ccw;
  }

  public synchronized void reapConnections() {
    long stale = System.currentTimeMillis() - timeout;
    Enumeration<JDCConnection> connlist = connections.elements();

    while ((connlist != null) && (connlist.hasMoreElements())) {
      JDCConnection conn = connlist.nextElement();

      if ((conn.inUse()) && (stale > conn.getLastUse()) && (!conn.validate())) {
        removeConnection(conn);
      }
    }
  }

  public synchronized void closeConnections() {
    Enumeration<JDCConnection> connlist = connections.elements();

    while ((connlist != null) && (connlist.hasMoreElements())) {
      JDCConnection conn = connlist.nextElement();
      removeConnection(conn);
    }
  }

  private synchronized void removeConnection(JDCConnection conn) {
    connections.removeElement(conn);
  }

  public synchronized Connection getConnection() throws SQLException {
	  if(this.ccw == null) {
		  JDCConnection c;
		  for (int i = 0; i < connections.size(); i++) {
			  c = connections.elementAt(i);
			  if (c.lease()) {
				  return c;
			  }
		  }

	    Connection conn = DriverManager.getConnection(url, user, password);
	    c = new JDCConnection(conn, this);
	    c.lease();
	    connections.addElement(c);
	    return c.getConnection();
	  }
	  else {
		  return ccw.getConnection();
	  }
  }

  public synchronized void returnConnection(JDCConnection conn) {
    conn.expireLease();
  }
}

class ConnectionReaper extends Thread {
  private ConnectionService pool;
  private final long delay = 300000;

  ConnectionReaper(ConnectionService pool) {
    this.pool = pool;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
      }
      pool.reapConnections();
    }
  }
}