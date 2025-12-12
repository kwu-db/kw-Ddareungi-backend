package com.kw.Ddareungi.domain.station.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.station.dto.RequestRegisterStation;
import com.kw.Ddareungi.domain.station.dto.ResponseStationList;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;
import com.kw.Ddareungi.domain.station.service.StationCommandService;
import com.kw.Ddareungi.domain.station.service.StationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대여소 API")
@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationApiController {

    private final StationCommandService stationCommandService;
    private final StationQueryService stationQueryService;

    //TODO authorize 추가하기
    @Operation(summary = "대여소 등록하기", description = "새로운 대여소를 등록합니다.")
    @PostMapping
    public ApiResponseDto<Long> registerStation(@RequestBody RequestRegisterStation requestRegisterStation,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ApiResponseDto.onSuccess(stationCommandService.registerStation(username, requestRegisterStation));
    }

    @Operation(summary = "대여소 목록 조회하기")
    @GetMapping
    public ApiResponseDto<ResponseStationList> getStationList() {
        return ApiResponseDto.onSuccess(stationQueryService.getAllStationList());
    }
	@Operation(summary = "대여소 상세 조회하기")
	@GetMapping("/{stationId}")
	public ApiResponseDto<ResponseStationSpecific> getStationById(@PathVariable Long stationId) {
		return ApiResponseDto.onSuccess(stationQueryService.getStationSpecific(stationId));
	}

	@Operation(summary = "대여소 정보 수정하기", description = "대여소 정보를 수정합니다.")
	@PatchMapping("/{stationId}")
	public ApiResponseDto<Void> updateStation(
			@Parameter(description = "대여소 ID", required = true) @PathVariable Long stationId,
			@RequestBody RequestRegisterStation requestRegisterStation,
			@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails != null ? userDetails.getUsername() : null;
		stationCommandService.updateStation(stationId, requestRegisterStation, username);
		return ApiResponseDto.onSuccess(null);
	}

	@Operation(summary = "대여소 삭제하기", description = "대여소를 삭제합니다.")
	@DeleteMapping("/{stationId}")
	public ApiResponseDto<Void> deleteStation(
			@Parameter(description = "대여소 ID", required = true) @PathVariable Long stationId,
			@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails != null ? userDetails.getUsername() : null;
		stationCommandService.deleteStation(stationId, username);
		return ApiResponseDto.onSuccess(null);
	}

	@Operation(summary = "따릉이 데이터 동기화", description = "따릉이 API에서 대여소 데이터를 가져와 저장합니다.")
	@PostMapping("/sync")
	public ApiResponseDto<Integer> syncDdareungiStations() {
		int syncedCount = stationCommandService.syncDdareungiStations();
		return ApiResponseDto.onSuccess(syncedCount);
	}
}
