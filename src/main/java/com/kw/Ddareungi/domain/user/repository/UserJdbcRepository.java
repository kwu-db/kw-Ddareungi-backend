package com.kw.Ddareungi.domain.user.repository;

import com.kw.Ddareungi.domain.user.entity.Role;
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
			String roleStr = rs.getString("role");
			Role role = roleStr != null ? Role.valueOf(roleStr) : Role.USER;
			return User.builder()
					.id(rs.getLong("user_id"))
					.username(rs.getString("username"))
					.name(rs.getString("name"))
					.email(rs.getString("email"))
					.password(rs.getString("password"))
					.role(role)
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
				       role,
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
				       role,
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
				INSERT INTO users (username, name, email, password, role, created_date, last_modified_date)
				VALUES (:username, :name, :email, :password, :role, :createdDate, :lastModifiedDate)
				""";

		LocalDateTime now = LocalDateTime.now();
		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("username", user.getUsername())
				.addValue("name", user.getName())
				.addValue("email", user.getEmail())
				.addValue("password", user.getPassword())
				.addValue("role", user.getRole() != null ? user.getRole().name() : Role.USER.name())
				.addValue("createdDate", Optional.ofNullable(user.getCreatedDate()).orElse(now))
				.addValue("lastModifiedDate", Optional.ofNullable(user.getLastModifiedDate()).orElse(now));

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, params, keyHolder, new String[]{"user_id"});
		return Optional.ofNullable(keyHolder.getKey())
				.map(Number::longValue)
				.orElseThrow(() -> new IllegalStateException("사용자 저장 중 ID 생성에 실패했습니다."));
	}

	@Override
	public boolean existsByEmail(String email) {
		String sql = """
				SELECT COUNT(*) > 0
				  FROM users
				 WHERE email = :email
				""";
		Boolean result = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("email", email), Boolean.class);
		return result != null && result;
	}

	@Override
	public Optional<User> findByIdAndRole(Long id, Role role) {
		String sql = """
				SELECT user_id,
				       username,
				       name,
				       email,
				       password,
				       role,
				       created_date,
				       last_modified_date
				  FROM users
				 WHERE user_id = :userId AND role = :role
				""";
		try {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue("userId", id)
					.addValue("role", role.name());
			User user = jdbcTemplate.queryForObject(sql, params, USER_ROW_MAPPER);
			return Optional.ofNullable(user);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean existsByEmailExcludingId(String email, Long id) {
		String sql = """
				SELECT COUNT(*) > 0
				  FROM users
				 WHERE email = :email AND user_id != :userId
				""";
		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("email", email)
				.addValue("userId", id);
		Boolean result = jdbcTemplate.queryForObject(sql, params, Boolean.class);
		return result != null && result;
	}

	@Override
	public int updateAdminSelectively(Long adminId, String name, String email, String password) {
		StringBuilder sql = new StringBuilder("UPDATE users SET ");
		MapSqlParameterSource params = new MapSqlParameterSource("adminId", adminId);
		boolean hasUpdate = false;

		if (name != null) {
			sql.append("name = :name");
			params.addValue("name", name);
			hasUpdate = true;
		}
		if (email != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("email = :email");
			params.addValue("email", email);
			hasUpdate = true;
		}
		if (password != null) {
			if (hasUpdate) sql.append(", ");
			sql.append("password = :password");
			params.addValue("password", password);
			hasUpdate = true;
		}

		if (!hasUpdate) {
			return 0;
		}

		sql.append(", last_modified_date = :lastModifiedDate WHERE user_id = :adminId");
		params.addValue("lastModifiedDate", LocalDateTime.now());

		return jdbcTemplate.update(sql.toString(), params);
	}
}

