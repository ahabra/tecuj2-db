package com.tek271.util2.db;

import org.sql2o.Query;

import java.util.List;
import java.util.Map;

public class DbReader<E> extends DbAccessor<DbReader<E>> {
	private final Class<E> entityType;

	@Override
	protected DbReader<E> getThis() {
		return this;
	}

	public DbReader(Class<E> entityType) {
		this.entityType = entityType;
	}

	public List<E> read() {
		return dbConnection.isConnected()? read(dbConnection) : readAndClose();
	}

	private List<E> read(DbConnection con) {
		Query query = createQuery(con);
		return query.executeAndFetch(entityType);
	}

	private List<E> readAndClose() {
		try (DbConnection con = dbConnection.connect()) {
			return read(con);
		}
	}

	public List<Map<String, Object>> readMaps() {
		return dbConnection.isConnected()? readMaps(dbConnection) :  readMapsAnClose();
	}

	private List<Map<String, Object>> readMaps(DbConnection con) {
		Query query = createQuery(con);
		return query.executeAndFetchTable().asList();
	}

	private List<Map<String, Object>> readMapsAnClose() {
		try (DbConnection con = dbConnection.connect()) {
			return readMaps(con);
		}
	}

}
