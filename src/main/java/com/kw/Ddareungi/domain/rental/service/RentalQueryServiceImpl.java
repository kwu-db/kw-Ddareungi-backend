package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.ResponseRentalList;
import com.kw.Ddareungi.domain.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalQueryServiceImpl implements  RentalQueryService {

    private final RentalRepository rentalRepository;

    @Override
    public ResponseRentalList getCurrentRentalList(String username) {
        return null;
    }
}
