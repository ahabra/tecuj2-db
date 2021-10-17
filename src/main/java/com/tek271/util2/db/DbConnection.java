package com.tek271.util2.db;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Encapsulates a JDBC connection
 */
public class DbConnection implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(DbConnection.class);
	private String url, user, password;
	private Connection sql2oConnection;
	private boolean isTransaction = false;

	/** Define the JDBC driver's full class name */
	public DbConnection driver(String driver) {
		loadDriver(driver);
		return this;
	}

	private static void loadDriver(String driver) {
		if (StringUtils.isBlank(driver)) return;

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot load class " + driver, e);
		}
	}

	public DbConnection url(String url) {
		this.url = url;
		return this;
	}

	public DbConnection user(String user) {
		this.user = user;
		return this;
	}

	public DbConnection password(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Connect to the DB server. If you call this method multiple times on
	 * the same <code>DbConnection</code> object, it will return the same
	 * connection, as long as you did not close it.
	 * @return This object. Allows fluent programming.
	 */
	public DbConnection connect() {
		if (sql2oConnection == null) {
			sql2oConnection = sql2o().open();
		}
		return this;
	}

	private Sql2o sql2o() {
		logAction("Open");
		return new Sql2o(url, user, password);
	}

	private void logAction(String action) {
		LOGGER.debug(action + " connection to db: " + substringBefore(url, "?") + " for user=" + user);
	}

	/**
	 * Close the connection. If the connection is already closed, then nothing
	 * will happen.
	 */
	@Override
	public void close() {
		if (sql2oConnection != null) {
			logAction("Close");
			sql2oConnection.close();
			sql2oConnection = null;
		}
	}

	/** Check if there is a current DB connection */
	public boolean isConnected() {
		return sql2oConnection != null;
	}

	/** Start a transaction */
	public DbConnection transaction() {
		isTransaction = true;
		sql2oConnection = sql2o().beginTransaction();
		return this;
	}

	/** Commit current transaction */
	public DbConnection commit() {
		if (isTransaction) {
			sql2oConnection.commit();
		}
		isTransaction = false;
		return this;
	}

	/** Roll back current transaction */
	public DbConnection rollback() {
		if (isTransaction) {
			sql2oConnection.rollback();
		}
		isTransaction = false;
		return this;
	}


	/**
	 * Create a connected SQL query
	 * @param sql
	 * @return a Query object
	 * @throws IllegalArgumentException if the sql argument is empty
	 * @throws IllegalStateException if there was no connection
	 */
	Query createQuery(String sql) {
		LOGGER.debug(sql);
		sql = StringUtils.trim(sql);
		if (StringUtils.isEmpty(sql)) {
			throw new IllegalArgumentException("SQL statement cannot be empty");
		}
		if (!isConnected()) {
			throw new IllegalStateException("You must establish a connection before createQuery()");
		}
		return sql2oConnection.createQuery(sql);
	}

	<T> T getKeyOfLastInsert(Class<T> cls) {
		return sql2oConnection.getKey(cls);
	}

}
