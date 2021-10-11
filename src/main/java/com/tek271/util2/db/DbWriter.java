package com.tek271.util2.db;

import com.google.common.base.Splitter;
import org.sql2o.Query;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DbWriter extends DbAccessor<DbWriter> {
	private static final long NO_KEY = Long.MIN_VALUE;
	private boolean isReturnKey;

	protected DbWriter getThis() {
		return this;
	}

	public DbWriter returnKeyAfterWrite(boolean isReturnKey) {
		this.isReturnKey = isReturnKey;
		return this;
	}

	public long write() {
		if (isNotBlank(script)) {
			writeScript();
			return NO_KEY;
		}
		boolean isConnected = dbConnection.isConnected();
		if (!isConnected) dbConnection.connect();
		long key = write(dbConnection);
		if (!isConnected) dbConnection.close();
		return key;
	}

	private long write(DbConnection con) {
		Query query = createQuery(con);
		query.executeUpdate();
		if (isReturnKey) {
			return con.sql2oConnection.getKey(long.class);
		}
		return NO_KEY;
	}

	private void writeScript() {
		Iterable<String> queries = Splitter.on(";\n").trimResults().omitEmptyStrings().split(script);
		boolean isConnected = dbConnection.isConnected();
		if (!isConnected) dbConnection.connect();
		DbWriter dbWriter = new DbWriter().withDbConnection(dbConnection);

		for (String sql: queries) {
			dbWriter.sql(sql).write();
		}
		if (!isConnected) dbConnection.close();
	}

}
