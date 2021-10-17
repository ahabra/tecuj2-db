package com.tek271.util2.db;

import org.sql2o.Query;

import java.util.List;
import java.util.Map;

/**
 * Read from a table into an entity class or Map.
 * @param <E> The type of the entity, e.g. User, Book, ...
 */
public class DbReader<E> extends DbAccessor<DbReader<E>> {
	private final Class<E> entityType;

	/**
	 * Create an instance
	 * @param dbConnection a connection object
	 * @param entityType The type of the entity
	 */
	public DbReader(DbConnection dbConnection, Class<E> entityType) {
		super(dbConnection);
		this.entityType = entityType;
	}

	@Override
	protected DbReader<E> getThis() {
		return this;
	}

	/**
	 * Read from db into a list of Objects
	 * @return a list of entity objects
	 */
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

	/**
	 * Read from db into a list of Maps. Each map represent a single row in
	 * the result set.
	 */
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
