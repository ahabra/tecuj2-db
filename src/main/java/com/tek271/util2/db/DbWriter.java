package com.tek271.util2.db;

import com.google.common.base.Splitter;
import org.sql2o.Query;

public class DbWriter extends DbAccessor<DbWriter> {

	public DbWriter(DbConnection dbConnection) {
		super(dbConnection);
	}

	protected DbWriter getThis() {
		return this;
	}

	public void write() {
		writeAndReturnNewKey(null);
	}

	public long writeAndReturnNewKey() {
		return writeAndReturnNewKey(long.class);
	}

	public <T> T writeAndReturnNewKey(Class<T> classOfKey) {
		boolean isConnected = dbConnection.isConnected();
		if (!isConnected) dbConnection.connect();

		try {
			return write(dbConnection, classOfKey);
		} finally {
			if (!isConnected) dbConnection.close();
		}
	}

	private <T> T write(DbConnection con, Class<T> classOfKey) {
		Query query = createQuery(con);
		query.executeUpdate();
		if (classOfKey != null) {
			return con.getKeyOfLastInsert(classOfKey);
		}
		return null;
	}

	/**
	 * Run a list of "write" queries
	 * @param script lines of sql queries where each sql statement is terminated by a semicolon
	 */
	public void writeScript(String script) {
		Iterable<String> queries = Splitter.on(";\n").trimResults().omitEmptyStrings().split(script);
		boolean isConnected = dbConnection.isConnected();
		if (!isConnected) dbConnection.connect();

		try {
			queries.forEach(q -> sql(q).write(dbConnection, null));
		} finally {
			if (!isConnected) dbConnection.close();
		}
	}

	public void writeScriptFromFile(String fileName) {
		String script = resourceTools.readAsString(fileName);
		writeScript(script);
	}

}
