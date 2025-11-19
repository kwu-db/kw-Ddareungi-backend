package com.kw.Ddareungi.domain.user.repository;

import com.kw.Ddareungi.domain.user.entity.User;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_ROW_MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp created = rs.getTimestamp("created_date");
            Timestamp modified = rs.getTimestamp("last_modified_date");
            return User.builder()
                    .id(rs.getLong("user_id"))
                    .username(rs.getString("username"))
                    .name(rs.getString("name"))
                    .email(rs.getString("email"))
                    .password(rs.getString("password"))
                    .createdDate(created != null ? created.toLocalDateTime() : null)
                    .lastModifiedDate(modified != null ? modified.toLocalDateTime() : null)
                    .build();
        }
    };

    @Override
    public Optional<User> findById(Long id) {
        String sql = """
                SELECT user_id,
                       username,
                       name,
                       email,
                       password,
                       created_date,
                       last_modified_date
                  FROM users
                 WHERE user_id = :userId
                """;
        try {
            User user = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", id), USER_ROW_MAPPER);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT user_id,
                       username,
                       name,
                       email,
                       password,
                       created_date,
                       last_modified_date
                  FROM users
                 WHERE username = :username
                """;
        try {
            User user = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("username", username), USER_ROW_MAPPER);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return findByUsername(loginId);
    }

    @Override
    public Long save(User user) {
        String sql = """
                INSERT INTO users (username, name, email, password, created_date, last_modified_date)
                VALUES (:username, :name, :email, :password, :createdDate, :lastModifiedDate)
                """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("createdDate", Optional.ofNullable(user.getCreatedDate()).orElse(now))
                .addValue("lastModifiedDate", Optional.ofNullable(user.getLastModifiedDate()).orElse(now));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"user_id"});
        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("사용자 저장 중 ID 생성에 실패했습니다."));
    }
}

