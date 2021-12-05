package com.epam.esm.repository.config;

public class DatabaseParameter {
	private DatabaseParameter() {
		
	}
	
	public static final String DB_PROPERTY_FILE_NAME = "database";
	
	public static final String DB_DRIVER = "db.driverClassName";
	public static final String DB_URL = "db.url";
	public static final String DB_USERNAME = "db.username";
	public static final String DB_PASSWORD = "db.password";
	public static final String DB_MAX_POOL_SIZE = "db.pool.maxTotal";
	public static final String DB_INIT_POOL_SIZE = "db.pool.initSize";
	public static final String DB_MAX_IDLE = "db.pool.maxIdle";
	public static final String DB_MAX_WAIT = "db.pool.maxWait";
	

}
