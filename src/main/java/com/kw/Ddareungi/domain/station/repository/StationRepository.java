package com.kw.Ddareungi.domain.station.repository;

import com.kw.Ddareungi.domain.station.entity.Station;

import java.time.LocalDate;
import java.time.LocalTime;

public interface StationRepository {
	boolean existsByStationName(String stationName);
	boolean existsByStationNameExcludingId(String stationName, Long stationId);
	int updateStationSelectively(Long stationId, String stationName, String address,
	                             Double latitude, Double longitude, Integer capacity, Integer availableBikes,
	                             LocalDate installationDate, LocalTime closedDate, Long modifiedById);
	boolean existsById(Long stationId);
	void deleteById(Long stationId);
	Station findById(Long stationId);
	Long findIdByStationName(String stationName);
	Long save(Station station);
}

