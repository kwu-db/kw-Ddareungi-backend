package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalCommandServiceImpl implements RentalCommandService {
    private final RentalRepository rentalRepository;

    /**
     * SQL 입력
     * 1. stationId를 통해 station exist 확인
     * 2. username을 통해 User exist 확인
     * 3. rental insert
     * 4. station의 count 차감
     *
     * @param stationId
     * @param username
     * @return
     */
    @Override
    public Long rentalAt(Long stationId, String username) {
        return 0L;
    }

    /**
     * SQL 입력
     * 1. rentalId를 통해 rental 확인(where)
     * 2. username으로 validation
     * 3. 해당 rental의 종료 시간 입력
     * @param rentalId
     * @param username
     * @return
     */
    @Override
    public Long returnDdareungi(Long rentalId, String username) {
        return 0L;
    }
}
