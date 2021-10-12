package com.tek271.util2.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class DbQueriesTest {
	private DbQueries sut;

	@BeforeEach
	public void setUp() {
		sut = new DbQueries();
	}

	@Test
	public void getFailsIfEmpty() {
		Exception ex = assertThrows(NoSuchElementException.class, () -> sut.get("whatever"));
		assertTrue(ex.getMessage().contains("DbQueries is empty."));
	}


}