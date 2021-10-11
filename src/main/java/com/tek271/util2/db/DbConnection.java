package com.tek271.util2.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringBefore;

public class DbConnection implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(DbConnection.class);
	private String url, user, password;
	protected Connection sql2oConnection;
	private boolean isTransaction = false;

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

	public DbConnection params(String url, String user, String password) {
		return url(url).user(user).password(password);
	}

	public DbConnection params(Map<String, String> map) {
		return params(map.get("url"), map.get("user"), map.get("password"));
	}

	public DbConnection connect() {
		if (sql2oConnection == null) {
			sql2oConnection = sql2o().open();
		}
		return this;
	}

	private Sql2o sql2o() {
		LOGGER.debug("Connecting to db: " + substringBefore(url, "?") + " for user=" + user);
		return new Sql2o(url, user, password);
	}

	public void close() {
		if (sql2oConnection != null) {
			LOGGER.debug("Closing connection to db: " + substringBefore(url, "?") + " for user=" + user);
			sql2oConnection.close();
			sql2oConnection = null;
		}
	}

	public DbConnection transaction() {
		isTransaction = true;
		sql2oConnection = sql2o().beginTransaction();
		return this;
	}

	public DbConnection commit() {
		if (isTransaction) {
			sql2oConnection.commit();
		}
		return this;
	}

	public DbConnection rollback() {
		if (isTransaction) {
			sql2oConnection.rollback();
		}
		return this;
	}

	public boolean isConnected() {
		return sql2oConnection != null;
	}

}
