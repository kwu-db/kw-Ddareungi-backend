package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.ResponseRentalList;

public interface RentalQueryService {
    ResponseRentalList getCurrentRentalList(String username);
}
