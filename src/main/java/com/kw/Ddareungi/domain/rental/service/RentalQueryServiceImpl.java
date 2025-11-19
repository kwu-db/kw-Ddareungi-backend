package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.dto.ResponseRentalList;
import com.kw.Ddareungi.domain.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalQueryServiceImpl implements  RentalQueryService {

    private final RentalRepository rentalRepository;

    /**
     * SQL 입력
     * 1. username을 통해 JOIN (rental, station)
     * 2. return에 맞게 매핑
     * @param username
     * @return ResponseRentalList
     */
    @Override
    public ResponseRentalList getCurrentRentalList(String username) {
        return null;
    }
}
