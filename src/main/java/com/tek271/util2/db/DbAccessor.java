package com.tek271.util2.db;

import com.tek271.util2.file.ResourceTools;
import org.sql2o.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides common features for DB read/write operations.
 * @param <T> A subclass of this class
 */
public abstract class DbAccessor<T extends DbAccessor<?>> {
	protected final DbConnection dbConnection;
	protected final Map<String, Object> parameters = new HashMap<>();
	protected String sql;

	protected ResourceTools resourceTools = new ResourceTools();

	private final T thisObj; // used to simplify the builder pattern
	protected abstract T getThis();

	protected DbAccessor(DbConnection dbConnection) {
		this.dbConnection = dbConnection;
		thisObj = getThis();
	}

	/**
	 * Define a parameter name and value to be applied in the SQL query
	 * @param name Name of parameter
	 * @param value Value of parameter
	 * @return This object. It allows fluent programming.
	 */
	public T param(String name, Object value) {
		parameters.put(name, value);
		return thisObj;
	}

	/**
	 * Define a group of parameters as from a map
	 * @param params a Map of named parameters
	 * @return This object.
	 */
	public T params(Map<String, Object> params) {
		parameters.putAll(params);
		return thisObj;
	}

	/**
	 * Define the SQL query to run. This could be either read or write type of
	 * SQL statement. The query can have parameters prefixed by colon. For example:
	 * <code>
	 *   select * from user where id = :id
	 * </code>
	 * In the above query, we have a parameter named "id" is expected.
	 * <code>
	 *   select * from book where author_name = :name
	 * </code>
	 * In the above query have a parameter named "name". Note that you should not put
	 * quotes around the parameter.
	 * @param sql An SQL query to run.
	 * @return This object.
	 */
	public T sql(String sql) {
		this.sql = sql;
		return thisObj;
	}


	protected Query createQuery(DbConnection con) {
		Query query = con.createQuery(sql);
		parameters.forEach(query::addParameter);
		return query;
	}

}
