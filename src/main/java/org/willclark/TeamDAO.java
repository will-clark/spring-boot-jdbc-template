package org.willclark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class TeamDAO {
	
	private static final String TABLE = "TEAMS";
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
		
	public void create(Team team) {
		team.setCreated(LocalDateTime.now());
		team.setModified(LocalDateTime.now());
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
			new PreparedStatementCreator()  {
	            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	                PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + TABLE + " (CREATED, MODIFIED, CITY, NAME, COLORS) VALUES (?, ?, ?, ?, ?)", new String[]{"ID"});
	                
	                stmt.setTimestamp(1, JdbcTemplateUtils.toTimestamp(team.getCreated()));
	                stmt.setTimestamp(2, JdbcTemplateUtils.toTimestamp(team.getModified()));
	                stmt.setString(3, team.getCity());
	                stmt.setString(4, team.getName());
	                stmt.setArray(5, JdbcTemplateUtils.toSqlArray(connection, team.getColors()));
	
	                return stmt;
	            }
        }, keyHolder);
		
		team.setId(keyHolder.getKey().longValue());
	}
	
	public void update(Team team) {
		team.setModified(LocalDateTime.now());
		
		jdbcTemplate.update(
			new PreparedStatementCreator()  {
	            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	                PreparedStatement stmt = connection.prepareStatement("UPDATE " + TABLE + " SET MODIFIED=?, CITY=?, NAME=?, COLORS=? WHERE ID=?");
	                
	                stmt.setTimestamp(1, JdbcTemplateUtils.toTimestamp(team.getModified()));
	                stmt.setString(2, team.getCity());
	                stmt.setString(3, team.getName());
	                stmt.setArray(4, JdbcTemplateUtils.toSqlArray(connection, team.getColors()));

	                stmt.setLong(5, team.getId());

	                return stmt;
	            }
        });		
	}
	
	public void delete(Team team) {
		team.setDeleted(LocalDateTime.now());
		
		jdbcTemplate.update(
			new PreparedStatementCreator()  {
	            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	                PreparedStatement stmt = connection.prepareStatement("UPDATE " + TABLE + " SET DELETED=? WHERE ID=?");
	                
	                stmt.setTimestamp(1, JdbcTemplateUtils.toTimestamp(team.getDeleted()));
	                
	                stmt.setLong(2, team.getId());
	
	                return stmt;
	            }
        });
	}
	
	public Team find(long id) {
		List<Team> list = jdbcTemplate.query(
			new PreparedStatementCreator()  {
	            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
	                
	                stmt.setLong(1, id);
	
	                return stmt;
	            }
			}
		, parser());
		
		return (list != null && list.size() >= 1) ? list.get(0) : null;
	}

	public List<Team> findAll() {
		List<Team> list = jdbcTemplate.query(
			new PreparedStatementCreator()  {
	            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	                return connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE DELETED IS NULL");
	            }
			}
		, parser());
		
		return list;
	}
	
	private static RowMapper<Team> parser() {
		return new RowMapper<Team>() {
			@Override
			public Team mapRow(ResultSet rs, int rowId) throws SQLException, DataAccessException {
				Team team = new Team();
				
				team.setId(rs.getLong("ID"));
				team.setCreated(JdbcTemplateUtils.toLocalDateTime(rs.getTimestamp("CREATED")));
				team.setModified(JdbcTemplateUtils.toLocalDateTime(rs.getTimestamp("MODIFIED")));
				team.setDeleted(JdbcTemplateUtils.toLocalDateTime(rs.getTimestamp("DELETED")));
				team.setCity(rs.getString("CITY"));
				team.setName(rs.getString("NAME"));
				team.setColors(JdbcTemplateUtils.toList(rs.getArray("COLORS")));
				
				return team;
			}
		};
	}
	
}