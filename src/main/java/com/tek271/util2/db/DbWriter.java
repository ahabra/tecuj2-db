package com.tek271.util2.db;

import com.google.common.base.Splitter;
import org.sql2o.Query;

/**
 * Allow running 'write' type queries like insert, update, delete, create.
 */
public class DbWriter extends DbAccessor<DbWriter> {

	/**
	 * Create an instance
	 * @param dbConnection a connection object
	 */
	public DbWriter(DbConnection dbConnection) {
		super(dbConnection);
	}

	protected DbWriter getThis() {
		return this;
	}

	/** Run the defined SQL query */
	public void write() {
		writeAndReturnNewKey(null);
	}

	/**
	 * Run the defined SQL query and return the value of the newly created key as a 'long' value
	 * @return The 'long' value of the newly created key
	 */
	public long writeAndReturnNewKey() {
		return writeAndReturnNewKey(long.class);
	}

	/**
	 * Run the defined SQL query and return the value of the newly created key
	 * @param classOfKey Type of the key
	 * @param <T> The generic type of the key
	 * @return The value of the newly created key
	 */
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
	 * Run a sequence of "write" queries
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

	/**
	 * Read an SQL script from a file and execute it.
	 * The script will be a sequence of "write" queries.
	 * @param fileName Name of file/resource
	 */
	public void writeScriptFromFile(String fileName) {
		String script = resourceTools.readAsString(fileName);
		writeScript(script);
	}

}
