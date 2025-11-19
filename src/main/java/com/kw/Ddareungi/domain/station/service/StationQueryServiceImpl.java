package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.ResponseStationList;
import com.kw.Ddareungi.domain.station.dto.ResponseStationSpecific;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationQueryServiceImpl implements StationQueryService {

    private final StationQueryService stationQueryService;

    /**
     * SQL 입력
     * 1. station list 출력 (pagination)
     * 2. Pageable에 맞게
     * 3. return에 맞게 매핑
     * 주의 : pagination으로 할 수 있게끔 argument 추가
     * @return ResponseStationList
     */
    @Override
    public ResponseStationList getAllStationList() {
        return null;
    }

    /**
     * SQL 입력
     * 1. station id를 통해 station 정보 조회(where)
     * 2. return에 맞게 매핑
     * @param stationId
     * @return ResponseStationSpecific
     */
    @Override
    public ResponseStationSpecific getStationSpecific(Long stationId) {
        return null;
    }
}
