package com.tek271.util2.db;


import java.util.ArrayList;
import java.util.List;

public class TestHelper {


	public static DbConnection connect() {
		return new DbConnection().url("jdbc:hsqldb:mem:tecuj-test")
				.user("SA")
				.password("SA");
	}

	public static List<PlayEntity> insertPlayEntities(PlayEntity... entities) {
		DbWriter dbWriter = new DbWriter(connect());

		List<PlayEntity> result = new ArrayList<>();
		for (PlayEntity e: entities) {
			e.id = dbWriter.param("name", e.name)
					.param("date", e.date)
					.sql(PlayEntity.INSERT_SQL)
					.writeAndReturnNewKey();
			result.add(e);
		}
		return result;
	}

	public static List<PlayEntity> insertPlayEntities(int count) {
		PlayEntity[]  entities = new PlayEntity[count];
		for (int i=0; i<count; i++) {
			entities[i] = PlayEntity.create(i);
		}
		return insertPlayEntities(entities);
	}

}
