package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.ResponseStation;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationQueryServiceImpl implements StationQueryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<ResponseStation> STATION_ROW_MAPPER = new RowMapper<>() {
        @Override
        public ResponseStation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date installation = rs.getDate("installation_date");
            Time closed = rs.getTime("closed_date");
            LocalDate installationDate = installation != null ? installation.toLocalDate() : null;
            LocalTime closedTime = closed != null ? closed.toLocalTime() : null;

            return ResponseStation.builder()
                    .stationId(rs.getLong("station_id"))
                    .stationName(rs.getString("station_name"))
                    .latitude(rs.getDouble("latitude"))
                    .longitude(rs.getDouble("longitude"))
                    .address(rs.getString("address"))
                    .capacity(rs.getInt("capacity"))
                    .availableBikes(rs.getInt("available_bikes"))
                    .installationDate(installationDate)
                    .closedDate(closedTime)
                    .build();
        }
    };

    @Override
    public Page<ResponseStation> getAllStationList(String search, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String condition = "";

        if (StringUtils.hasText(search)) {
            condition = " WHERE station_name LIKE :search";
            params.addValue("search", "%" + search + "%");
        }

        // 전체 개수 조회
        String countSql = "SELECT COUNT(*) FROM station" + condition;
        long total = jdbcTemplate.queryForObject(countSql, params, Long.class);

        // 페이지네이션 파라미터 추가
        MapSqlParameterSource pagingParams = new MapSqlParameterSource();
        params.getValues().forEach(pagingParams::addValue);
        pagingParams
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        // 데이터 조회
        String dataSql = """
                SELECT station_id, station_name, latitude, longitude, address, capacity, available_bikes, installation_date, closed_date
                  FROM station
                """ + condition + """
                 ORDER BY station_name ASC
                 LIMIT :limit OFFSET :offset
                """;

        List<ResponseStation> stations = jdbcTemplate.query(dataSql, pagingParams, STATION_ROW_MAPPER);

        return new PageImpl<>(stations, pageable, total);
    }

    @Override
    public ResponseStationSpecific getStationSpecific(Long stationId) {
        String sql = """
                SELECT station_id, station_name, latitude, longitude, address, capacity, available_bikes, installation_date, closed_date
                  FROM station
                 WHERE station_id = :stationId
                """;
        try {
            ResponseStation station = jdbcTemplate.queryForObject(
                    sql,
                    new MapSqlParameterSource("stationId", stationId),
                    STATION_ROW_MAPPER
            );
            return ResponseStationSpecific.builder()
                    .responseStation(station)
                    .build();
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("대여소를 찾을 수 없습니다.");
        }
    }
}
