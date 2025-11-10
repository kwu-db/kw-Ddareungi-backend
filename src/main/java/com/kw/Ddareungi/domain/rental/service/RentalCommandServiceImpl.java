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

    @Override
    public Long rentalAt(Long stationId, String username) {
        return 0L;
    }

    @Override
    public Long returnDdareungi(Long rentalId, String username) {
        return 0L;
    }
}
