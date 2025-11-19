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

    /**
     * SQL 입력
     * 1. username을 생성자로 입력
     * 2. argument에 맞게 입력
     * 3. Station 생성
     * @param requestRegisterStation
     * @return
     */
    @Override
    public Long registerStation(String username, RequestRegisterStation requestRegisterStation) {
        return 0L;
    }



}
