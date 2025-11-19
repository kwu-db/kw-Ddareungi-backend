package com.kw.Ddareungi.domain.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ResponseRentalList {

    @Builder.Default
    private List<RentalInfo> rentals = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalInfo {
        private Long rentalId;
        private String bikeNumber;
        private String startStationName;
        private String endStationName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
    }
}
