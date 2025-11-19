package com.kw.Ddareungi.domain.station.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ResponseStationList {
    @Builder.Default
    private List<ResponseStation> stationList = new ArrayList<>();
}
