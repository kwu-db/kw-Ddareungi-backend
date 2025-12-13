package com.kw.Ddareungi.domain.rental.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.rental.dto.RankingListResponseDto;
import com.kw.Ddareungi.domain.rental.dto.ResponseRentalList;
import com.kw.Ddareungi.domain.rental.dto.RentalResponseDto;
import com.kw.Ddareungi.domain.rental.service.RentalCommandService;
import com.kw.Ddareungi.domain.rental.service.RentalQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[유저 페이지]대여 API")
@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalApiController {

    private final RentalQueryService rentalQueryService;
    private final RentalCommandService rentalCommandService;

    //TODO filter by status
    @Operation(summary = "대여현황 목록 조회하기")
    @GetMapping
    public ApiResponseDto<ResponseRentalList> getRental(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ApiResponseDto.onSuccess(rentalQueryService.getCurrentRentalList(username));
    }

    @Operation(summary = "대여하기")
    @PostMapping("/stations/{stationId}")
    public ApiResponseDto<Long> rentalDdareungi(@PathVariable Long stationId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ApiResponseDto.onSuccess(rentalCommandService.rentalAt(stationId, username));
    }
	@Operation(summary = "반납하기")
	@PatchMapping("/{rentalId}")
	public ApiResponseDto<Long> returnDdareungi(@PathVariable Long rentalId,
												@AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails != null ? userDetails.getUsername() : null;
		return ApiResponseDto.onSuccess(rentalCommandService.returnDdareungi(rentalId, username));
	}

	@Operation(summary = "유저 자전거 대여 내역 조회", description = "특정 유저의 자전거 대여 내역을 조회합니다.")
	@GetMapping("/users/{userId}")
	public ApiResponseDto<List<RentalResponseDto>> getRentalsByUserId(
			@Parameter(description = "유저 ID", required = true) @PathVariable Long userId) {
		return ApiResponseDto.onSuccess(rentalQueryService.getRentalsByUserId(userId));
	}

	@Operation(summary = "이용횟수 랭킹 조회", description = "자전거 대여 횟수 기준 랭킹을 조회합니다.")
	@GetMapping("/rankings/count")
	public ApiResponseDto<RankingListResponseDto> getRentalCountRanking(
			@Parameter(description = "랭킹 조회 개수 (기본값: 10)", required = false)
			@RequestParam(defaultValue = "10") int limit) {
		return ApiResponseDto.onSuccess(rentalQueryService.getRentalCountRanking(limit));
	}

	@Operation(summary = "이용시간 랭킹 조회", description = "자전거 총 이용시간(분) 기준 랭킹을 조회합니다.")
	@GetMapping("/rankings/time")
	public ApiResponseDto<RankingListResponseDto> getRentalTimeRanking(
			@Parameter(description = "랭킹 조회 개수 (기본값: 10)", required = false)
			@RequestParam(defaultValue = "10") int limit) {
		return ApiResponseDto.onSuccess(rentalQueryService.getRentalTimeRanking(limit));
	}

}
