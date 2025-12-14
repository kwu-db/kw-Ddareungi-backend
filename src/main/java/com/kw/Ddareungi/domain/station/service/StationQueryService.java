package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.ResponseStation;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StationQueryService {
    Page<ResponseStation> getAllStationList(String search, Pageable pageable);

    ResponseStationSpecific getStationSpecific(Long stationId);
}
