package com.kw.Ddareungi.domain.rental.repository;

import com.kw.Ddareungi.domain.rental.entity.Rental;

import java.util.List;

public interface RentalRepository {
	List<Rental> findAllByUserIdOrderByCreatedDateDesc(Long userId);
	boolean existsUserById(Long userId);
}

