package com.kw.Ddareungi.domain.station.service;

import com.kw.Ddareungi.domain.station.client.DdareungiApiClient;
import com.kw.Ddareungi.domain.station.dto.DdareungiApiResponseDto;
import com.kw.Ddareungi.domain.station.dto.RequestRegisterStation;
import com.kw.Ddareungi.domain.station.entity.Station;
import com.kw.Ddareungi.domain.station.repository.StationRepository;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import com.kw.Ddareungi.global.exception.ErrorStatus;
import com.kw.Ddareungi.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StationCommandServiceImpl implements StationCommandService {

	private final UserRepository userRepository;
	private final StationRepository stationRepository;
	private final DdareungiApiClient ddareungiApiClient;
	private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long registerStation(String username, RequestRegisterStation requestRegisterStation) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        Station station = Station.builder()
                .stationName(requestRegisterStation.getStationName())
                .latitude(requestRegisterStation.getLatitude())
                .longitude(requestRegisterStation.getLongitude())
                .address(requestRegisterStation.getAddress())
                .capacity(requestRegisterStation.getCapacity())
                .availableBikes(0)
                .installationDate(requestRegisterStation.getInstallationDate())
                .closedDate(requestRegisterStation.getClosedDate())
                .createdById(user.getId())
                .modifiedById(user.getId())
                .createdDate(now)
                .lastModifiedDate(now)
                .build();

        return stationRepository.save(station);
	}

	@Override
	public void updateStation(Long stationId, RequestRegisterStation requestRegisterStation, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Station station = stationRepository.findById(stationId);
		if (station == null) {
			throw new GeneralException(ErrorStatus.STATION_NOT_FOUND);
		}

		if (!station.getStationName().equals(requestRegisterStation.getStationName())
				&& stationRepository.existsByStationNameExcludingId(requestRegisterStation.getStationName(), stationId)) {
			throw new GeneralException(ErrorStatus.STATION_ALREADY_EXISTS);
		}

		int updatedRows = stationRepository.updateStationSelectively(
				stationId,
				requestRegisterStation.getStationName(),
				requestRegisterStation.getAddress(),
				requestRegisterStation.getLatitude(),
				requestRegisterStation.getLongitude(),
				requestRegisterStation.getCapacity(),
				station.getAvailableBikes(),
				requestRegisterStation.getInstallationDate(),
				requestRegisterStation.getClosedDate(),
				user.getId()
		);

		if (updatedRows == 0) {
			throw new GeneralException(ErrorStatus.STATION_NOT_FOUND);
		}
	}

	@Override
	public void deleteStation(Long stationId, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!stationRepository.existsById(stationId)) {
			throw new GeneralException(ErrorStatus.STATION_NOT_FOUND);
		}

		stationRepository.deleteById(stationId);
	}

	@Override
	public int syncDdareungiStations(String username) {
		Long userId = null;
		if (username != null) {
			User user = userRepository.findByUsername(username).orElse(null);
			if (user != null) {
				userId = user.getId();
			}
		}

		DdareungiApiResponseDto apiResponse = ddareungiApiClient.fetchAllStationData();
		
		if (apiResponse == null || 
			apiResponse.getRentBikeStatus() == null ||
			apiResponse.getRentBikeStatus().getRow() == null ||
			apiResponse.getRentBikeStatus().getRow().isEmpty()) {
			log.warn("따릉이 API에서 데이터를 가져올 수 없습니다.");
			return 0;
		}

		List<DdareungiApiResponseDto.StationInfo> stationInfos = apiResponse.getRentBikeStatus().getRow();
		int syncedCount = 0;
		int updatedCount = 0;
		int createdCount = 0;

		LocalDateTime now = LocalDateTime.now();

		for (DdareungiApiResponseDto.StationInfo stationInfo : stationInfos) {
			try {
				String stationName = stationInfo.getStationName();
				if (stationName == null || stationName.trim().isEmpty()) {
					log.warn("대여소 이름이 없어 스킵합니다: {}", stationInfo);
					continue;
				}

				Double latitude = parseDouble(stationInfo.getStationLatitude());
				Double longitude = parseDouble(stationInfo.getStationLongitude());
				
				if (latitude == null || longitude == null) {
					log.warn("위도/경도 정보가 없어 스킵합니다: {}", stationName);
					continue;
				}

				Integer capacity = parseInt(stationInfo.getRackTotCnt());
				if (capacity == null || capacity <= 0) {
					capacity = 0;
				}

				Integer availableBikes = parseInt(stationInfo.getParkingBikeTotCnt());
				if (availableBikes == null || availableBikes < 0) {
					availableBikes = 0;
				}

				String address = "";

				boolean exists = stationRepository.existsByStationName(stationName);

				if (exists) {
					Long stationId = stationRepository.findIdByStationName(stationName);
					
					if (stationId != null) {
						int activeRentals = countActiveRentalsByStationId(stationId);
						int adjustedAvailableBikes = Math.max(0, availableBikes - activeRentals);
						
						stationRepository.updateStationSelectively(
								stationId,
								stationName,
								address,
								latitude,
								longitude,
								capacity,
								adjustedAvailableBikes,
								null,
								null,
								userId
						);
						updatedCount++;
					}
				} else {
					Station newStation = Station.builder()
							.stationName(stationName)
							.latitude(latitude)
							.longitude(longitude)
							.address(address)
							.capacity(capacity)
							.availableBikes(availableBikes)
							.installationDate(LocalDate.now())
							.closedDate(null)
							.createdById(userId)
							.modifiedById(userId)
					.createdDate(now)
					.lastModifiedDate(now)
					.build();

					stationRepository.save(newStation);
					createdCount++;
				}
				syncedCount++;
			} catch (Exception e) {
				log.error("대여소 동기화 중 오류 발생: {}", stationInfo.getStationName(), e);
			}
		}

		log.info("따릉이 API 동기화 완료: 전체 {}개, 신규 {}개, 업데이트 {}개", syncedCount, createdCount, updatedCount);

		return syncedCount;
	}

	private Double parseDouble(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			log.warn("숫자 파싱 실패: {}", value);
			return null;
		}
	}

	private Integer parseInt(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			log.warn("정수 파싱 실패: {}", value);
			return null;
		}
	}

	private int countActiveRentalsByStationId(Long stationId) {
		String sql = """
				SELECT COUNT(*)
				  FROM rental
				 WHERE start_station_id = :stationId
				   AND end_time IS NULL
				""";
		try {
			Integer count = jdbcTemplate.queryForObject(
					sql,
					new MapSqlParameterSource("stationId", stationId),
					Integer.class);
			return count != null ? count : 0;
		} catch (Exception e) {
			log.warn("활성 대여 건수 조회 실패: stationId={}", stationId, e);
			return 0;
		}
	}
}
