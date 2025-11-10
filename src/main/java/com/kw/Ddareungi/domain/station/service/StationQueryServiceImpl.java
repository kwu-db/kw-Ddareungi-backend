package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.ResponseStationList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationQueryServiceImpl implements StationQueryService {

    private final StationQueryService stationQueryService;
    @Override
    public ResponseStationList getAllStationList() {
        return null;
    }
}
