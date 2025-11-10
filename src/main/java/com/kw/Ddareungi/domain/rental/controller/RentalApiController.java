package com.kw.Ddareungi.domain.rental.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.pass.service.PassQueryService;
import com.kw.Ddareungi.domain.rental.ResponseRentalList;
import com.kw.Ddareungi.domain.rental.entity.Rental;
import com.kw.Ddareungi.domain.rental.service.RentalCommandService;
import com.kw.Ddareungi.domain.rental.service.RentalQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponseDto<ResponseRentalList> getRental(@AuthenticationPrincipal String username) {
        return ApiResponseDto.onSuccess(rentalQueryService.getCurrentRentalList(username));
    }

    @Operation(summary = "대여하기")
    @PostMapping("/stations/{stationId}")
    public ApiResponseDto<Long> rentalDdareungi(@PathVariable Long stationId,
                                                @AuthenticationPrincipal String username) {
        return ApiResponseDto.onSuccess(rentalCommandService.rentalAt(stationId, username));
    }
    @Operation(summary = "반납하기")
    @PatchMapping("/{rentalId}")
    public ApiResponseDto<Long> returnDdareungi(@PathVariable Long rentalId,
                                                @AuthenticationPrincipal String username) {
        return ApiResponseDto.onSuccess(rentalCommandService.returnDdareungi(rentalId, username));
    }

}
