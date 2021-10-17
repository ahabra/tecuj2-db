package com.tek271.util2.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.tek271.util2.db.TestHelper.connect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.tek271.util2.db.PlayEntity.*;

public class DbWriterTest {
	private DbReader<PlayEntity> dbReader;
	private DbWriter dbWriter;

	@BeforeEach
	public void setUp() {
		dbReader = new DbReader<>(connect(), PlayEntity.class);
		dbWriter = new DbWriter(connect());

		dbWriter.sql("drop table if exists PlayEntity;").write();
		dbWriter.writeScriptFromFile("DbReaderTest.sql");
	}

	@Test
	public void testTransaction() {
		List<PlayEntity> entities = newArrayList(create(1), create(2));
		try (DbConnection con = dbWriter.dbConnection.transaction()) {
			for (PlayEntity e: entities) {
				e.id = dbWriter.param("name", e.name)
						.param("date", e.date)
						.sql(PlayEntity.INSERT_SQL)
						.writeAndReturnNewKey();
			}
			con.commit();
		}
		List<PlayEntity> found = dbReader.sql("select * from PlayEntity").read();
		assertEquals(entities, found);
	}

	@Test
	public void testWriteQuery() {
		PlayEntity playEntity = create(1);
		dbWriter
				.param("name", playEntity.name)
				.param("date", playEntity.date)
				.sql(PlayEntity.INSERT_SQL)
				.write();
		List<PlayEntity> found = dbReader.sql("select * from PlayEntity").read();
		assertEquals(1, found.size());
		assertEquals(playEntity, found.get(0));
	}

	@Test
	public void testWriteQueryAndGetId() {
		PlayEntity playEntity = create(1);
		long id = dbWriter
				.param("name", playEntity.name)
				.param("date", playEntity.date)
				.sql(PlayEntity.INSERT_SQL)
				.writeAndReturnNewKey();
		List<PlayEntity> found = dbReader.sql("select * from PlayEntity").read();
		assertEquals(0, id);
		assertEquals(1, found.size());
		assertEquals(playEntity, found.get(0));
	}

}