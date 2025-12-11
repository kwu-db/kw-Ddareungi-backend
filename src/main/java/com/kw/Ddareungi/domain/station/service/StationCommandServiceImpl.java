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
                .availableBikes(0) // 수동 등록 시 초기값 0
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
		// 사용자 확인 (registerStation과 일관성 유지)
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 존재 확인
		Station station = stationRepository.findById(stationId);
		if (station == null) {
			throw new GeneralException(ErrorStatus.STATION_NOT_FOUND);
		}

		// 이름이 변경되는 경우 중복 체크
		if (!station.getStationName().equals(requestRegisterStation.getStationName())
				&& stationRepository.existsByStationNameExcludingId(requestRegisterStation.getStationName(), stationId)) {
			throw new GeneralException(ErrorStatus.STATION_ALREADY_EXISTS);
		}

		// 선택적 업데이트
		int updatedRows = stationRepository.updateStationSelectively(
				stationId,
				requestRegisterStation.getStationName(),
				requestRegisterStation.getAddress(),
				requestRegisterStation.getLatitude(),
				requestRegisterStation.getLongitude(),
				requestRegisterStation.getCapacity(),
				requestRegisterStation.getInstallationDate(),
				requestRegisterStation.getClosedDate()
		);

		if (updatedRows == 0) {
			throw new GeneralException(ErrorStatus.STATION_NOT_FOUND);
		}
	}

	@Override
	public void deleteStation(Long stationId, String username) {
		// 사용자 확인 (registerStation과 일관성 유지)
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!stationRepository.existsById(stationId)) {
			throw new GeneralException(ErrorStatus.STATION_NOT_FOUND);
		}

		stationRepository.deleteById(stationId);
	}

	@Override
	public int syncDdareungiStations() {
		log.info("따릉이 API에서 대여소 데이터 동기화 시작");

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

				// 위도, 경도 파싱
				Double latitude = parseDouble(stationInfo.getStationLatitude());
				Double longitude = parseDouble(stationInfo.getStationLongitude());
				
				if (latitude == null || longitude == null) {
					log.warn("위도/경도 정보가 없어 스킵합니다: {}", stationName);
					continue;
				}

				// 수용 가능 대수 파싱 (총 수용 가능 대수)
				Integer capacity = parseInt(stationInfo.getRackTotCnt());
				if (capacity == null || capacity <= 0) {
					capacity = 0; // 기본값
				}

				// 현재 주차된 자전거 수 파싱
				Integer availableBikes = parseInt(stationInfo.getParkingBikeTotCnt());
				if (availableBikes == null || availableBikes < 0) {
					availableBikes = 0; // 기본값
				}

				// 주소는 API에서 제공하지 않으므로 빈 문자열로 설정
				String address = "";

				// 이미 존재하는 대여소인지 확인
				boolean exists = stationRepository.existsByStationName(stationName);

				if (exists) {
					// 기존 대여소 업데이트
					Long stationId = stationRepository.findIdByStationName(stationName);
					
					if (stationId != null) {
						// 우리 서비스에서 해당 대여소에서 대여 중인 건수 확인
						int activeRentals = countActiveRentalsByStationId(stationId);
						
						// API 데이터는 실제 따릉이의 자전거 수
						// 우리 서비스에서 대여한 건은 우리 DB에만 기록되므로
						// available_bikes = API의 parkingBikeTotCnt - 우리 서비스의 활성 대여 건수
						int adjustedAvailableBikes = Math.max(0, availableBikes - activeRentals);
						
						// 선택적 업데이트 (위치, 수용량, 현재 자전거 수 업데이트)
						stationRepository.updateStationSelectively(
								stationId,
								stationName, // 이름은 그대로
								address,
								latitude,
								longitude,
								capacity,
								adjustedAvailableBikes, // 우리 서비스 대여 건을 고려한 자전거 수
								null, // installationDate
								null  // closedDate
						);
						updatedCount++;
					}
				} else {
					// 새 대여소 등록
					Station newStation = Station.builder()
							.stationName(stationName)
							.latitude(latitude)
							.longitude(longitude)
							.address(address)
							.capacity(capacity)
							.availableBikes(availableBikes)
							.installationDate(null)
							.closedDate(null)
							.createdById(null) // API 동기화는 시스템 작업
							.modifiedById(null)
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

		log.info("따릉이 API 동기화 완료: 전체 {}개, 신규 {}개, 업데이트 {}개", 
				syncedCount, createdCount, updatedCount);

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
