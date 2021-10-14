package com.tek271.util2.db;

import com.tek271.util2.file.ResourceTools;
import org.sql2o.Query;

import java.util.HashMap;
import java.util.Map;

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

	public T param(String name, Object value) {
		parameters.put(name, value);
		return thisObj;
	}

	public T params(Map<String, Object> params) {
		parameters.putAll(params);
		return thisObj;
	}

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
