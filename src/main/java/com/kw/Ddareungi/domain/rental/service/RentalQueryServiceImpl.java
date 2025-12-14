package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.dto.RankingListResponseDto;
import com.kw.Ddareungi.domain.rental.dto.RankingResponseDto;
import com.kw.Ddareungi.domain.rental.dto.ResponseRentalList;
import com.kw.Ddareungi.domain.rental.dto.RentalResponseDto;
import com.kw.Ddareungi.domain.rental.repository.RentalRepository;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import com.kw.Ddareungi.global.exception.ErrorStatus;
import com.kw.Ddareungi.global.exception.GeneralException;
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
	private final RentalRepository rentalRepository;

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

	@Override
	public List<RentalResponseDto> getRentalsByUserId(Long userId) {
		// 사용자 존재 확인
		if (!rentalRepository.existsUserById(userId)) {
			throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
		}

		// 대여 내역 조회 (JDBC - 대여소 이름 포함)
		String sql = """
				SELECT r.rental_id,
				       r.bike_number,
				       r.user_id,
				       r.start_station_id,
				       r.end_station_id,
				       r.start_time,
				       r.end_time,
				       r.created_date,
				       ss.station_name AS start_station_name,
				       es.station_name AS end_station_name
				  FROM rental r
				  LEFT JOIN station ss ON ss.station_id = r.start_station_id
				  LEFT JOIN station es ON es.station_id = r.end_station_id
				 WHERE r.user_id = :userId
				 ORDER BY r.created_date DESC
				""";

		RowMapper<RentalResponseDto> rowMapper = new RowMapper<>() {
			@Override
			public RentalResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				Timestamp start = rs.getTimestamp("start_time");
				Timestamp end = rs.getTimestamp("end_time");
				Timestamp created = rs.getTimestamp("created_date");
				return RentalResponseDto.builder()
						.rentalId(rs.getLong("rental_id"))
						.startStationId(rs.getObject("start_station_id", Long.class))
						.endStationId(rs.getObject("end_station_id", Long.class))
						.startStationName(rs.getString("start_station_name"))
						.endStationName(rs.getString("end_station_name"))
						.bikeNum(rs.getString("bike_number"))
						.startTime(start != null ? start.toLocalDateTime() : null)
						.endTime(end != null ? end.toLocalDateTime() : null)
						.createdDate(created != null ? created.toLocalDateTime() : null)
						.build();
			}
		};

		return jdbcTemplate.query(sql, new MapSqlParameterSource("userId", userId), rowMapper);
	}

	@Override
	public RankingListResponseDto getRentalCountRanking(int limit) {
		String sql = """
				SELECT u.user_id,
				       u.name AS user_name,
				       COUNT(r.rental_id) AS rental_count
				  FROM users u
				  LEFT JOIN rental r ON r.user_id = u.user_id AND r.end_time IS NOT NULL
				 GROUP BY u.user_id, u.name
				 ORDER BY rental_count DESC, u.name ASC
				 LIMIT :limit
				""";

		List<RankingResponseDto> rankings = jdbcTemplate.query(
				sql,
				new MapSqlParameterSource("limit", limit),
				new RowMapper<RankingResponseDto>() {
					@Override
					public RankingResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
						return RankingResponseDto.builder()
								.rank((long) (rowNum + 1))
								.userId(rs.getLong("user_id"))
								.userName(rs.getString("user_name"))
								.value(rs.getLong("rental_count"))
								.build();
					}
				}
		);

		return RankingListResponseDto.builder()
				.rankings(rankings)
				.build();
	}

	@Override
	public RankingListResponseDto getRentalTimeRanking(int limit) {
		String sql = """
				SELECT u.user_id,
				       u.name AS user_name,
				       COALESCE(SUM(TIMESTAMPDIFF(SECOND, r.start_time, r.end_time)), 0) AS total_seconds
				  FROM users u
				  LEFT JOIN rental r ON r.user_id = u.user_id AND r.end_time IS NOT NULL
				 GROUP BY u.user_id, u.name
				 ORDER BY total_seconds DESC, u.name ASC
				 LIMIT :limit
				""";

		List<RankingResponseDto> rankings = jdbcTemplate.query(
				sql,
				new MapSqlParameterSource("limit", limit),
				new RowMapper<RankingResponseDto>() {
					@Override
					public RankingResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
						long totalSeconds = rs.getLong("total_seconds");
						long hours = totalSeconds / 3600;
						long minutes = (totalSeconds % 3600) / 60;
						long seconds = totalSeconds % 60;
						
						return RankingResponseDto.builder()
								.rank((long) (rowNum + 1))
								.userId(rs.getLong("user_id"))
								.userName(rs.getString("user_name"))
								.value(totalSeconds)
								.hours(hours)
								.minutes(minutes)
								.seconds(seconds)
								.build();
					}
				}
		);

		return RankingListResponseDto.builder()
				.rankings(rankings)
				.build();
	}
}
