package com.kw.Ddareungi.domain.rental.service;

import com.kw.Ddareungi.domain.rental.dto.RankingListResponseDto;
import com.kw.Ddareungi.domain.rental.dto.ResponseRentalList;
import com.kw.Ddareungi.domain.rental.dto.RentalResponseDto;

import java.util.List;

public interface RentalQueryService {
	ResponseRentalList getCurrentRentalList(String username);

	List<RentalResponseDto> getRentalsByUserId(Long userId);

	RankingListResponseDto getRentalCountRanking(int limit);

	RankingListResponseDto getRentalTimeRanking(int limit);
}
