package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.RequestRegisterStation;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;
import com.kw.Ddareungi.domain.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StationCommandServiceImpl implements StationCommandService {
    private final StationRepository stationRepository;
    @Override
    public Long registerStation(RequestRegisterStation requestRegisterStation) {
        return 0L;
    }

    @Override
    public ResponseStationSpecific getStationSpecific(Long stationId) {
        return null;
    }
}
