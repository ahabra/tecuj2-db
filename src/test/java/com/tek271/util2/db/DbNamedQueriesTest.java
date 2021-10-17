package com.tek271.util2.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class DbNamedQueriesTest {
	private DbNamedQueries sut;

	@BeforeEach
	public void setUp() {
		sut = new DbNamedQueries();
	}

	@Test
	public void getFailsIfEmpty() {
		Exception ex = assertThrows(NoSuchElementException.class, () -> sut.get("whatever"));
		assertTrue(ex.getMessage().contains("DbNamedQueries is empty."));
	}

	@Test
	void readsQueriesFromFile() {
		DbNamedQueries queries = sut.readFile("DbNamedQueriesTest.yml");
		assertEquals(3, sut.size());

		assertEquals("select * from user where id = :id", sut.get("findUserById"));
		assertEquals(sut, queries);
	}

	@Test
	void canCreateFromMap() {
		Map<String, String> map = Map.of("k1", "v1", "k2", "v2");
		sut = new DbNamedQueries(map);
		assertEquals(map, sut);
	}

}