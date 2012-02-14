package com.playblack.logblock.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

public class CanaryConnectionWrapper {
	Object etc; //dirty shit ...
	
	public CanaryConnectionWrapper(Object etc) {
		this.etc = etc;
	}
	
	public Connection getConnection() {
		Connection conn = null;
		try {
			conn = (Connection) etc.getClass().getMethod("getConnection", (Class<?>)null).invoke(etc, (Object[])null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
}
