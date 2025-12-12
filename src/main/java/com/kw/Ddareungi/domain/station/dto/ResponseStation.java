package com.kw.Ddareungi.domain.station.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
public class ResponseStation {
    private Long stationId;
    private String stationName;
    private Double latitude;
    private Double longitude;
    private String address;
    private Integer capacity;
    private Integer availableBikes;
    private LocalDate installationDate;
    private LocalTime closedDate;
}
