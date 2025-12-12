package com.kw.Ddareungi.domain.station.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DdareungiApiResponseDto {
    @JsonProperty("rentBikeStatus")
    private RentBikeStatus rentBikeStatus;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RentBikeStatus {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<StationInfo> row;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationInfo {
        @JsonProperty("rackTotCnt")
        private String rackTotCnt; // 거치대개수

        @JsonProperty("parkingBikeTotCnt")
        private String parkingBikeTotCnt; // 자전거주차총건수

        @JsonProperty("shared")
        private String shared; // 거치율

        @JsonProperty("stationLatitude")
        private String stationLatitude; // 위도

        @JsonProperty("stationLongitude")
        private String stationLongitude; // 경도

        @JsonProperty("stationId")
        private String stationId; // 대여소ID

        @JsonProperty("stationName")
        private String stationName; // 대여소이름
    }
}

