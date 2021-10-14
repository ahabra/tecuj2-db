package com.tek271.util2.db;

import com.tek271.util2.file.ResourceTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Query;

import java.util.HashMap;
import java.util.Map;

public abstract class DbAccessor<T extends DbAccessor<?>> {
	private static final Logger LOGGER = LogManager.getLogger(DbAccessor.class);

	protected DbConnection dbConnection;
	protected final Map<String, Object> parameters = new HashMap<>();
	protected String sql;
	protected String script;  // script is a list of sql queries

	protected ResourceTools resourceTools = new ResourceTools();

	private final T thisObj; // used to simplify the builder pattern
	protected abstract T getThis();

	public DbAccessor() {
		thisObj = getThis();
	}

	public T withDbConnection(DbConnection dbConnection) {
		this.dbConnection = dbConnection;
		return thisObj;
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
		this.script = null;
		return thisObj;
	}

	public T script(String script) {
		this.script = script;
		this.sql = null;
		return thisObj;
	}

	public T scriptFromFile(String fileName) {
		String text = resourceTools.readAsString(fileName);
		return script(text);
	}

	protected Query createQuery(DbConnection con) {
		LOGGER.debug(sql);
		Query query = con.sql2oConnection.createQuery(sql);
		if (parameters != null) {
			parameters.forEach(query::addParameter);
		}
		return query;
	}

}
