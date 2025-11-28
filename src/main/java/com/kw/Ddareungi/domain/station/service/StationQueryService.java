package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.ResponseStationList;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;

public interface StationQueryService {
    ResponseStationList getAllStationList();

    ResponseStationSpecific getStationSpecific(Long stationId);
}
