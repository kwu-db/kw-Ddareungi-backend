package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.RequestRegisterStation;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StationCommandServiceImpl implements StationCommandService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public Long registerStation(String username, RequestRegisterStation requestRegisterStation) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("stationName", requestRegisterStation.getStationName())
                .addValue("latitude", requestRegisterStation.getLatitude())
                .addValue("longitude", requestRegisterStation.getLongitude())
                .addValue("address", requestRegisterStation.getAddress())
                .addValue("capacity", requestRegisterStation.getCapacity())
                .addValue("installationDate", requestRegisterStation.getInstallationDate())
                .addValue("closedDate", requestRegisterStation.getClosedDate())
                .addValue("createdById", user.getId())
                .addValue("modifiedById", user.getId())
                .addValue("createdDate", now)
                .addValue("lastModifiedDate", now);

        String sql = """
                INSERT INTO station (station_name, latitude, longitude, address, capacity,
                                     installation_date, closed_date, created_by_id, modified_by_id,
                                     created_date, last_modified_date)
                VALUES (:stationName, :latitude, :longitude, :address, :capacity,
                        :installationDate, :closedDate, :createdById, :modifiedById,
                        :createdDate, :lastModifiedDate)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"station_id"});

        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("대여소 등록 중 오류가 발생했습니다."));
    }
}
