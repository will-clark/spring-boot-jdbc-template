package org.willclark;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplateUtils {
	
	public static Array toSqlArray(Connection connection, List<String> list) throws SQLException {
		if (list == null) return null;
		
		List<Object> array = new ArrayList<Object>(0);
		
		for (String each : list) {
			array.add(each);
		}
		
		return connection.createArrayOf("varchar", array.toArray(new Object[array.size()]));		
	}
	
	public static List<String> toList(Array array) throws SQLException {
		if (array == null) return null;
		
		List<String> list = new ArrayList<String>(0);
		
		ResultSet rs = array.getResultSet();
		
		while (rs.next()) {
			list.add(rs.getString(1));
		}
		
		rs.close();
		rs = null;
		
		return list;
	}
	
	public static Timestamp toTimestamp(LocalDateTime dateTime) {
		if (dateTime == null) return null;
		
		return Timestamp.valueOf(dateTime);
	}
	
	public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
		if (timestamp == null) return null;
		
		return timestamp.toLocalDateTime();
	}
	
}
