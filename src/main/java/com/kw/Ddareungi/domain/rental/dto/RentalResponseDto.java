package com.kw.Ddareungi.domain.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalResponseDto {
	private Long rentalId;
	private Long startStationId;
	private String startStationName;
	private Long endStationId;
	private String endStationName;
	private String bikeNum;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private LocalDateTime createdDate;
}

