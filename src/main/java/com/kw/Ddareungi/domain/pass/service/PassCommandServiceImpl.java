package com.kw.Ddareungi.domain.pass.service;

import com.kw.Ddareungi.domain.pass.entity.PassType;
import com.kw.Ddareungi.domain.pass.entity.UserPassStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PassCommandServiceImpl implements PassCommandService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public Long buyPass(Long passId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        PassType passType = findPassType(passId);

        LocalDate activatedDate = LocalDate.now();
        LocalDate expiredDate = calculateExpiredDate(passType, activatedDate);

        String sql = """
                INSERT INTO user_pass (user_id, pass_id, activated_date, expired_date, user_pass_status, created_date, last_modified_date)
                VALUES (:userId, :passId, :activatedDate, :expiredDate, :status, :createdDate, :lastModifiedDate)
                """;
        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("passId", passId)
                .addValue("activatedDate", activatedDate)
                .addValue("expiredDate", expiredDate)
                .addValue("status", UserPassStatus.ACTIVATE.name())
                .addValue("createdDate", now)
                .addValue("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"user_pass_id"});
        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("이용권 구매 중 문제가 발생했습니다."));
    }

    private PassType findPassType(Long passId) {
        String sql = "SELECT pass_type FROM pass WHERE pass_id = :passId";
        try {
            String type = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("passId", passId), String.class);
            if (type == null) {
                throw new IllegalArgumentException("존재하지 않는 이용권입니다.");
            }
            return PassType.valueOf(type);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("존재하지 않는 이용권입니다.");
        }
    }

    private LocalDate calculateExpiredDate(PassType passType, LocalDate activatedDate) {
        return switch (passType) {
            case ONE_DAY -> activatedDate.plusDays(1);
            case ONE_WEEK -> activatedDate.plusWeeks(1);
            case ONE_MONTH -> activatedDate.plusMonths(1);
            case ONE_YEAR -> activatedDate.plusYears(1);
        };
    }
}
