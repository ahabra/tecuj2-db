package com.tek271.util2.db;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PlayEntity {
	public static final String INSERT_SQL = "insert into PlayEntity (name, date) values (:name, :date)";
	private static final String DATE= "2016.09.07";
	public long id;
	public String name;
	public String date;

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static PlayEntity create(int counter) {
		PlayEntity e = new PlayEntity();
		e.name = "name-" + counter;
		e.date = DATE;
		return e;
	}

}
