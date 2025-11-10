package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.RequestRegisterStation;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;

public interface StationCommandService {
    Long registerStation(RequestRegisterStation requestRegisterStation);

    ResponseStationSpecific getStationSpecific(Long stationId);
}
