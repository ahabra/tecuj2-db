package com.tek271.util2.db;

import com.tek271.util2.file.YamlTools;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Define a map of named queries. Use this to put the SQL queries text in a
 * file that you can read at runtime. This helps specially with long SQL queries
 * that look ugly as long strings inside the Java code.
 */
public class DbNamedQueries extends HashMap<String, String> {

	public DbNamedQueries() {}

	public DbNamedQueries(Map<String, String> queries) {
		this.putAll(queries);
	}

	/**
	 * Define queries from a YAML file. Will replace any prior values.
	 * Each key in the YAML file will become a key in this map,
	 * while the value will become the SQL value in this map.
	 * @param queryFile the path to a YAML file
	 * @return this object.
	 */
	public DbNamedQueries readFile(String queryFile) {
		clear();
		putAll(new YamlTools().readFile(queryFile));
		return this;
	}

	/**
	 * Get a query by its name
	 * @param queryName Name of query
	 * @return the query's text
	 */
	public String get(String queryName) {
		if (isEmpty()) {
			String className = this.getClass().getSimpleName();
			throw new NoSuchElementException(className + " is empty. Make sure that you initialize it.");
		}
		return super.get(queryName);
	}

}
