package com.kw.Ddareungi.domain.station.repository;

import com.kw.Ddareungi.domain.station.entity.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StationJdbcRepository implements StationRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private static final RowMapper<Station> STATION_ROW_MAPPER = new RowMapper<>() {
		@Override
		public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
			Timestamp created = rs.getTimestamp("created_date");
			Timestamp modified = rs.getTimestamp("last_modified_date");
			Date installation = rs.getDate("installation_date");
			Time closed = rs.getTime("closed_date");
			return Station.builder()
					.id(rs.getLong("station_id"))
					.stationName(rs.getString("station_name"))
					.latitude(rs.getDouble("latitude"))
					.longitude(rs.getDouble("longitude"))
					.address(rs.getString("address"))
					.capacity(rs.getInt("capacity"))
					.availableBikes(rs.getInt("available_bikes"))
					.installationDate(installation != null ? installation.toLocalDate() : null)
					.closedDate(closed != null ? closed.toLocalTime() : null)
					.createdById(rs.getObject("created_by_id", Long.class))
					.modifiedById(rs.getObject("modified_by_id", Long.class))
					.createdDate(created != null ? created.toLocalDateTime() : null)
					.lastModifiedDate(modified != null ? modified.toLocalDateTime() : null)
					.build();
		}
	};

	@Override
	public boolean existsByStationName(String stationName) {
		String sql = """
				SELECT COUNT(*) > 0
				  FROM station
				 WHERE station_name = :stationName
				""";
		Boolean result = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("stationName", stationName), Boolean.class);
		return result != null && result;
	}

	@Override
	public boolean existsByStationNameExcludingId(String stationName, Long stationId) {
		String sql = """
				SELECT COUNT(*) > 0
				  FROM station
				 WHERE station_name = :stationName AND station_id != :stationId
				""";
		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("stationName", stationName)
				.addValue("stationId", stationId);
		Boolean result = jdbcTemplate.queryForObject(sql, params, Boolean.class);
		return result != null && result;
	}

	@Override
	public int updateStationSelectively(Long stationId, String stationName, String address,
	                                    Double latitude, Double longitude, Integer capacity, Integer availableBikes,
	                                    LocalDate installationDate, LocalTime closedDate) {
		StringBuilder sql = new StringBuilder("UPDATE station SET ");
		MapSqlParameterSource params = new MapSqlParameterSource("stationId", stationId);
		boolean hasUpdate = false;

		if (stationName != null) {
			sql.append("station_name = :stationName");
			params.addValue("stationName", stationName);
			hasUpdate = true;
		}
		if (address != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("address = :address");
			params.addValue("address", address);
			hasUpdate = true;
		}
		if (latitude != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("latitude = :latitude");
			params.addValue("latitude", latitude);
			hasUpdate = true;
		}
		if (longitude != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("longitude = :longitude");
			params.addValue("longitude", longitude);
			hasUpdate = true;
		}
		if (capacity != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("capacity = :capacity");
			params.addValue("capacity", capacity);
			hasUpdate = true;
		}
		if (availableBikes != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("available_bikes = :availableBikes");
			params.addValue("availableBikes", availableBikes);
			hasUpdate = true;
		}
		if (installationDate != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("installation_date = :installationDate");
			params.addValue("installationDate", Date.valueOf(installationDate));
			hasUpdate = true;
		}
		if (closedDate != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("closed_date = :closedDate");
			params.addValue("closedDate", Time.valueOf(closedDate));
			hasUpdate = true;
		}

		if (!hasUpdate) {
			return 0;
		}

		sql.append(", last_modified_date = :lastModifiedDate WHERE station_id = :stationId");
		params.addValue("lastModifiedDate", LocalDateTime.now());

		return jdbcTemplate.update(sql.toString(), params);
	}

	@Override
	public boolean existsById(Long stationId) {
		String sql = """
				SELECT COUNT(*) > 0
				  FROM station
				 WHERE station_id = :stationId
				""";
		Boolean result = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("stationId", stationId), Boolean.class);
		return result != null && result;
	}

	@Override
	public void deleteById(Long stationId) {
		String sql = """
				DELETE FROM station
				 WHERE station_id = :stationId
				""";
		jdbcTemplate.update(sql, new MapSqlParameterSource("stationId", stationId));
	}

	@Override
	public Station findById(Long stationId) {
		String sql = """
				SELECT station_id,
				       station_name,
				       latitude,
				       longitude,
				       address,
				       capacity,
				       available_bikes,
				       installation_date,
				       closed_date,
				       created_by_id,
				       modified_by_id,
				       created_date,
				       last_modified_date
				  FROM station
				 WHERE station_id = :stationId
				""";
		try {
			return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("stationId", stationId), STATION_ROW_MAPPER);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Long findIdByStationName(String stationName) {
		String sql = """
				SELECT station_id
				  FROM station
				 WHERE station_name = :stationName
				 LIMIT 1
				""";
		try {
			return jdbcTemplate.queryForObject(
					sql,
					new MapSqlParameterSource("stationName", stationName),
					Long.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Long save(Station station) {
		String sql = """
				INSERT INTO station (station_name, latitude, longitude, address, capacity, available_bikes,
				                     installation_date, closed_date, created_by_id, modified_by_id,
				                     created_date, last_modified_date)
				VALUES (:stationName, :latitude, :longitude, :address, :capacity, :availableBikes,
				        :installationDate, :closedDate, :createdById, :modifiedById,
				        :createdDate, :lastModifiedDate)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("stationName", station.getStationName())
				.addValue("latitude", station.getLatitude())
				.addValue("longitude", station.getLongitude())
				.addValue("address", station.getAddress())
				.addValue("capacity", station.getCapacity())
				.addValue("availableBikes", station.getAvailableBikes())
				.addValue("installationDate", station.getInstallationDate() != null ? Date.valueOf(station.getInstallationDate()) : null)
				.addValue("closedDate", station.getClosedDate() != null ? Time.valueOf(station.getClosedDate()) : null)
				.addValue("createdById", station.getCreatedById())
				.addValue("modifiedById", station.getModifiedById())
				.addValue("createdDate", station.getCreatedDate() != null ? station.getCreatedDate() : LocalDateTime.now())
				.addValue("lastModifiedDate", station.getLastModifiedDate() != null ? station.getLastModifiedDate() : LocalDateTime.now());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, params, keyHolder, new String[]{"station_id"});
		return Optional.ofNullable(keyHolder.getKey())
				.map(Number::longValue)
				.orElseThrow(() -> new IllegalStateException("대여소 저장 중 ID 생성에 실패했습니다."));
	}
}

