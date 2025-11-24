package com.kw.Ddareungi.domain.rental.repository;

import com.kw.Ddareungi.domain.rental.entity.Rental;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RentalJdbcRepository implements RentalRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private static final RowMapper<Rental> RENTAL_ROW_MAPPER = new RowMapper<>() {
		@Override
		public Rental mapRow(ResultSet rs, int rowNum) throws SQLException {
			Timestamp created = rs.getTimestamp("created_date");
			Timestamp modified = rs.getTimestamp("last_modified_date");
			Timestamp startTime = rs.getTimestamp("start_time");
			Timestamp endTime = rs.getTimestamp("end_time");
			return Rental.builder()
					.id(rs.getLong("rental_id"))
					.bikeNumber(rs.getString("bike_number"))
					.userId(rs.getLong("user_id"))
					.startStationId(rs.getObject("start_station_id", Long.class))
					.endStationId(rs.getObject("end_station_id", Long.class))
					.startTime(startTime != null ? startTime.toLocalDateTime() : null)
					.endTime(endTime != null ? endTime.toLocalDateTime() : null)
					.createdDate(created != null ? created.toLocalDateTime() : null)
					.lastModifiedDate(modified != null ? modified.toLocalDateTime() : null)
					.build();
		}
	};

	@Override
	public List<Rental> findAllByUserIdOrderByCreatedDateDesc(Long userId) {
		String sql = """
				SELECT rental_id,
				       bike_number,
				       user_id,
				       start_station_id,
				       end_station_id,
				       start_time,
				       end_time,
				       created_date,
				       last_modified_date
				  FROM rental
				 WHERE user_id = :userId
				 ORDER BY created_date DESC
				""";
		return jdbcTemplate.query(sql, new MapSqlParameterSource("userId", userId), RENTAL_ROW_MAPPER);
	}

	@Override
	public boolean existsUserById(Long userId) {
		String sql = """
				SELECT COUNT(*) > 0
				  FROM users
				 WHERE user_id = :userId
				""";
		Boolean result = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", userId), Boolean.class);
		return result != null && result;
	}
}

