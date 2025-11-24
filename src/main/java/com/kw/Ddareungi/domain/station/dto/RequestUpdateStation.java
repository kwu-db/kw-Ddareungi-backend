package com.kw.Ddareungi.domain.station.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@Jacksonized
public class RequestUpdateStation {
	private String stationName;
	private Double latitude;
	private Double longitude;
	private String address;
	private Integer capacity;
	private LocalDate installationDate;
	private LocalTime closedDate;
}

