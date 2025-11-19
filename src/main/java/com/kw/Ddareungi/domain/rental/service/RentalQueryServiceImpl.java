package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.dto.ResponseRentalList;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalQueryServiceImpl implements RentalQueryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    private static final RowMapper<ResponseRentalList.RentalInfo> RENTAL_ROW_MAPPER = new RowMapper<>() {
        @Override
        public ResponseRentalList.RentalInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp start = rs.getTimestamp("start_time");
            Timestamp end = rs.getTimestamp("end_time");
            LocalDateTime startTime = start != null ? start.toLocalDateTime() : null;
            LocalDateTime endTime = end != null ? end.toLocalDateTime() : null;
            String status = endTime == null ? "IN_PROGRESS" : "COMPLETED";

            return ResponseRentalList.RentalInfo.builder()
                    .rentalId(rs.getLong("rental_id"))
                    .bikeNumber(rs.getString("bike_number"))
                    .startStationName(rs.getString("start_station_name"))
                    .endStationName(rs.getString("end_station_name"))
                    .startTime(startTime)
                    .endTime(endTime)
                    .status(status)
                    .build();
        }
    };

    @Override
    public ResponseRentalList getCurrentRentalList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String sql = """
                SELECT r.rental_id,
                       r.bike_number,
                       r.start_time,
                       r.end_time,
                       ss.station_name AS start_station_name,
                       es.station_name AS end_station_name
                  FROM rental r
                  LEFT JOIN station ss ON ss.station_id = r.start_station_id
                  LEFT JOIN station es ON es.station_id = r.end_station_id
                 WHERE r.user_id = :userId
                 ORDER BY r.start_time DESC
                """;

        List<ResponseRentalList.RentalInfo> rentals = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("userId", user.getId()),
                RENTAL_ROW_MAPPER
        );

        return ResponseRentalList.builder()
                .rentals(rentals)
                .build();
    }
}
