package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalCommandServiceImpl implements RentalCommandService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public Long rentalAt(Long stationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // station available_bikes check & decrement
        ensureStationExists(stationId);
        int updated = jdbcTemplate.update(
                "UPDATE station SET available_bikes = available_bikes - 1 WHERE station_id = :stationId AND available_bikes > 0",
                new MapSqlParameterSource("stationId", stationId)
        );
        if (updated == 0) {
            throw new IllegalStateException("대여 가능한 자전거가 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("bikeNumber", generateBikeNumber(stationId))
                .addValue("startStationId", stationId)
                .addValue("startTime", now)
                .addValue("createdDate", now)
                .addValue("lastModifiedDate", now);

        String sql = """
                INSERT INTO rental (user_id, bike_number, start_station_id, start_time, created_date, last_modified_date)
                VALUES (:userId, :bikeNumber, :startStationId, :startTime, :createdDate, :lastModifiedDate)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"rental_id"});

        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("대여 등록 중 오류가 발생했습니다."));
    }

    @Override
    public Long returnDdareungi(Long rentalId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RentalRecord record = fetchActiveRental(rentalId);
        if (!record.userId.equals(user.getId())) {
            throw new IllegalArgumentException("해당 대여건을 반납할 권한이 없습니다.");
        }
        if (record.endTime != null) {
            throw new IllegalStateException("이미 반납이 완료된 대여건입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("rentalId", rentalId)
                .addValue("endTime", now)
                .addValue("endStationId", record.startStationId)
                .addValue("lastModifiedDate", now);

        String updateSql = """
                UPDATE rental
                   SET end_time = :endTime,
                       end_station_id = :endStationId,
                       last_modified_date = :lastModifiedDate
                 WHERE rental_id = :rentalId
                """;
        jdbcTemplate.update(updateSql, params);

        // increase station available_bikes back
        jdbcTemplate.update(
                "UPDATE station SET available_bikes = available_bikes + 1 WHERE station_id = :stationId",
                new MapSqlParameterSource("stationId", record.startStationId)
        );

        return rentalId;
    }

    private void ensureStationExists(Long stationId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM station WHERE station_id = :stationId",
                new MapSqlParameterSource("stationId", stationId),
                Long.class
        );
        if (count == null || count == 0) {
            throw new IllegalArgumentException("대여소를 찾을 수 없습니다.");
        }
    }

    private RentalRecord fetchActiveRental(Long rentalId) {
        String sql = """
                SELECT rental_id, user_id, start_station_id, end_time
                  FROM rental
                 WHERE rental_id = :rentalId
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("rentalId", rentalId), (rs, rowNum) ->
                    new RentalRecord(
                            rs.getLong("rental_id"),
                            rs.getLong("user_id"),
                            rs.getLong("start_station_id"),
                            rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("대여 정보를 찾을 수 없습니다.");
        }
    }

    private String generateBikeNumber(Long stationId) {
        return "BIKE-" + stationId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private record RentalRecord(Long rentalId, Long userId, Long startStationId, LocalDateTime endTime) {
    }
}
