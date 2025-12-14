package com.kw.Ddareungi.domain.pass.repository;

import com.kw.Ddareungi.domain.pass.entity.Pass;
import com.kw.Ddareungi.domain.pass.entity.PassType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PassJdbcRepository implements PassRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<Pass> PASS_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Pass mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp created = rs.getTimestamp("created_date");
            Timestamp modified = rs.getTimestamp("last_modified_date");
            return Pass.builder()
                    .id(rs.getLong("pass_id"))
                    .passType(PassType.valueOf(rs.getString("pass_type")))
                    .price(rs.getInt("price"))
                    .createdDate(created != null ? created.toLocalDateTime() : null)
                    .lastModifiedDate(modified != null ? modified.toLocalDateTime() : null)
                    .build();
        }
    };

    @Override
    public Optional<Pass> findById(Long passId) {
        String sql = """
                SELECT pass_id, pass_type, price, created_date, last_modified_date
                  FROM pass
                 WHERE pass_id = :passId
                """;
        try {
            Pass pass = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("passId", passId), PASS_ROW_MAPPER);
            return Optional.ofNullable(pass);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Pass> findAll() {
        String sql = """
                SELECT pass_id, pass_type, price, created_date, last_modified_date
                  FROM pass
                 ORDER BY pass_id ASC
                """;
        return jdbcTemplate.query(sql, PASS_ROW_MAPPER);
    }

    @Override
    public Long save(Pass pass) {
        String sql = """
                INSERT INTO pass (pass_type, price, created_date, last_modified_date)
                VALUES (:passType, :price, :createdDate, :lastModifiedDate)
                """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("passType", pass.getPassType().name())
                .addValue("price", pass.getPrice())
                .addValue("createdDate", Optional.ofNullable(pass.getCreatedDate()).orElse(now))
                .addValue("lastModifiedDate", Optional.ofNullable(pass.getLastModifiedDate()).orElse(now));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"pass_id"});
        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("이용권 저장 중 ID 생성에 실패했습니다."));
    }

    @Override
    public boolean existsById(Long passId) {
        String sql = """
                SELECT COUNT(*) > 0
                  FROM pass
                 WHERE pass_id = :passId
                """;
        Boolean result = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("passId", passId), Boolean.class);
        return result != null && result;
    }
}

