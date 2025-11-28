package com.kw.Ddareungi.domain.rental.service;

public interface RentalCommandService {
    Long rentalAt(Long stationId, String username);

    Long returnDdareungi(Long rentalId, String username);

}
