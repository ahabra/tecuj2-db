package com.tek271.util2.db;

import com.tek271.util2.file.YamlTools;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class DbNamedQueries extends HashMap<String, String> {

	public DbNamedQueries() {}

	public DbNamedQueries(Map<String, String> queries) {
		this.putAll(queries);
	}

	public DbNamedQueries readFile(String queryFile) {
		clear();
		putAll(new YamlTools().readFile(queryFile));
		return this;
	}

	public String get(String queryName) {
		if (isEmpty()) {
			throw new NoSuchElementException("DbQueries is empty. Please make sure that you initialize it.");
		}
		return super.get(queryName);
	}

}
