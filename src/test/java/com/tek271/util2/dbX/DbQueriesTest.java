package com.tek271.util2.dbX;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class DbQueriesTest {
	private DbQueries sut;

	@BeforeEach
	public void setUp() {
		sut = new DbQueries();
		DbQueries.QUERY_CACHE.clear();
	}

	@Test
	public void getFailsIfEmpty() {
		Exception ex = assertThrows(NoSuchElementException.class, () -> {
			sut.get("whatever");
		});
		assertTrue(ex.getMessage().contains("DbQueries is empty."));
	}

	@Test
	public void cacheGetFailsIfEmpty() {
		Exception ex = assertThrows(NoSuchElementException.class, () -> {
			DbQueries.QUERY_CACHE.get("whatever");
		});

		assertTrue(ex.getMessage().contains("DbQueries is empty."));
	}

	@Test
	public void cacheCanBeInitialized() {
		DbQueries.initCache("YamlToolsTest.yml");
		String q1 = DbQueries.QUERY_CACHE.get("q1");
		assertEquals("select * from t1", q1);
	}

}