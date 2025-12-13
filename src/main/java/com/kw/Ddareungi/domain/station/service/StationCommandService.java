package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.dto.RequestRegisterStation;

public interface StationCommandService {
	Long registerStation(String username, RequestRegisterStation requestRegisterStation);

	void updateStation(Long stationId, RequestRegisterStation requestRegisterStation, String username);

	void deleteStation(Long stationId, String username);

	int syncDdareungiStations(String username);
}
