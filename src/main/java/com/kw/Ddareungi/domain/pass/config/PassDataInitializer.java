package com.kw.Ddareungi.domain.pass.config;

import com.kw.Ddareungi.domain.pass.entity.Pass;
import com.kw.Ddareungi.domain.pass.entity.PassType;
import com.kw.Ddareungi.domain.pass.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class PassDataInitializer implements CommandLineRunner {

    private final PassRepository passRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();

        // 1일권 확인 및 생성
        if (!existsByPassType(PassType.ONE_DAY)) {
            Pass oneDayPass = Pass.builder()
                    .passType(PassType.ONE_DAY)
                    .price(1000)
                    .createdDate(now)
                    .lastModifiedDate(now)
                    .build();
            passRepository.save(oneDayPass);
            log.info("1일권 이용권이 생성되었습니다. (가격: 1,000원)");
        }

        // 1주일권 확인 및 생성
        if (!existsByPassType(PassType.ONE_WEEK)) {
            Pass oneWeekPass = Pass.builder()
                    .passType(PassType.ONE_WEEK)
                    .price(5000)
                    .createdDate(now)
                    .lastModifiedDate(now)
                    .build();
            passRepository.save(oneWeekPass);
            log.info("1주일권 이용권이 생성되었습니다. (가격: 5,000원)");
        }

        // 1개월권 확인 및 생성
        if (!existsByPassType(PassType.ONE_MONTH)) {
            Pass oneMonthPass = Pass.builder()
                    .passType(PassType.ONE_MONTH)
                    .price(15000)
                    .createdDate(now)
                    .lastModifiedDate(now)
                    .build();
            passRepository.save(oneMonthPass);
            log.info("1개월권 이용권이 생성되었습니다. (가격: 15,000원)");
        }

        // 1년권 확인 및 생성
        if (!existsByPassType(PassType.ONE_YEAR)) {
            Pass oneYearPass = Pass.builder()
                    .passType(PassType.ONE_YEAR)
                    .price(100000)
                    .createdDate(now)
                    .lastModifiedDate(now)
                    .build();
            passRepository.save(oneYearPass);
            log.info("1년권 이용권이 생성되었습니다. (가격: 100,000원)");
        }
    }

    private boolean existsByPassType(PassType passType) {
        String sql = """
                SELECT COUNT(*) > 0
                  FROM pass
                 WHERE pass_type = :passType
                """;
        Boolean result = jdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("passType", passType.name()),
                Boolean.class
        );
        return result != null && result;
    }
}

