package com.kw.Ddareungi.domain.pass.service;

import com.kw.Ddareungi.domain.pass.dto.ResponsePass;
import com.kw.Ddareungi.domain.pass.dto.ResponsePassList;
import com.kw.Ddareungi.domain.pass.dto.ResponseUserPass;
import com.kw.Ddareungi.domain.pass.dto.ResponseUserPassList;
import com.kw.Ddareungi.domain.pass.entity.PassType;
import com.kw.Ddareungi.domain.pass.entity.UserPassStatus;
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
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassQueryServiceImpl implements PassQueryService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    private static final RowMapper<ResponsePass> PASS_MAPPER = new RowMapper<>() {
        @Override
        public ResponsePass mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ResponsePass.builder()
                    .passId(rs.getLong("pass_id"))
                    .passType(PassType.valueOf(rs.getString("pass_type")))
                    .price(rs.getInt("price"))
                    .build();
        }
    };

    private static final RowMapper<ResponseUserPass> USER_PASS_MAPPER = new RowMapper<>() {
        @Override
        public ResponseUserPass mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp activated = rs.getTimestamp("activated_date");
            Timestamp expired = rs.getTimestamp("expired_date");
            LocalDate activatedDate = activated != null ? activated.toLocalDateTime().toLocalDate() : null;
            LocalDate expiredDate = expired != null ? expired.toLocalDateTime().toLocalDate() : null;
            return ResponseUserPass.builder()
                    .userPassId(rs.getLong("user_pass_id"))
                    .passId(rs.getLong("pass_id"))
                    .passType(PassType.valueOf(rs.getString("pass_type")))
                    .price(rs.getInt("price"))
                    .status(UserPassStatus.valueOf(rs.getString("user_pass_status")))
                    .activatedDate(activatedDate)
                    .expiredDate(expiredDate)
                    .build();
        }
    };

    @Override
    public ResponsePassList getPassList() {
        String sql = """
                SELECT pass_id, pass_type, price
                  FROM pass
                 ORDER BY price ASC
                """;
        List<ResponsePass> passes = jdbcTemplate.query(sql, PASS_MAPPER);
        return ResponsePassList.builder()
                .passes(passes)
                .build();
    }

    @Override
    public ResponseUserPassList getUserPassList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String sql = """
                SELECT up.user_pass_id,
                       up.user_pass_status,
                       up.activated_date,
                       up.expired_date,
                       p.pass_id,
                       p.pass_type,
                       p.price
                  FROM user_pass up
                  JOIN pass p ON p.pass_id = up.pass_id
                 WHERE up.user_id = :userId
                 ORDER BY (up.activated_date IS NULL), up.activated_date DESC
                """;
        List<ResponseUserPass> userPasses = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("userId", user.getId()),
                USER_PASS_MAPPER
        );
        return ResponseUserPassList.builder()
                .userPasses(userPasses)
                .build();
    }
}
