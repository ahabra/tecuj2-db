package com.tek271.util2.db;

import com.tek271.util2.file.YamlTools;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class DbQueries extends HashMap<String, String> {
	public static final DbQueries QUERY_CACHE = new DbQueries();
	private static String cachedQueryFile;

	public DbQueries() {}

	public DbQueries(Map<String, String> queries) {
		this.putAll(queries);
	}

	public DbQueries readFile(String queryFile) {
		clear();
		putAll(new YamlTools().readFile(queryFile));
		return this;
	}

	public synchronized static DbQueries initCache(String queryFile) {
		if (StringUtils.equals(queryFile, cachedQueryFile)) {
			return QUERY_CACHE;
		}
		cachedQueryFile = queryFile;
		return QUERY_CACHE.readFile(queryFile);
	}

	public String get(String queryName) {
		if (isEmpty()) {
			throw new NoSuchElementException("DbQueries is empty. Plesse make sure that you initialize it.");
		}
		return super.get(queryName);
	}

}
