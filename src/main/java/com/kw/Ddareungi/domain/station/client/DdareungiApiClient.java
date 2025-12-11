package com.kw.Ddareungi.domain.station.client;

import com.kw.Ddareungi.domain.station.dto.DdareungiApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class DdareungiApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.ddareungi.api.url}")
    private String apiUrl;

    @Value("${app.ddareungi.api.key}")
    private String apiKey;

    @Value("${app.ddareungi.api.endpoint}")
    private String endpoint;

    public DdareungiApiResponseDto fetchStationData(int startIndex, int endIndex) {
        // URL 형식: http://openapi.seoul.go.kr:8088/{인증키}/json/bikeList/{시작}/{종료}/
        // 한 번에 최대 1,000건 초과 불가 (ERROR-336)
        if (endIndex - startIndex + 1 > 1000) {
            log.error("따릉이 API 호출 실패: 한 번에 최대 1,000건을 초과할 수 없습니다. (요청: {}건)", endIndex - startIndex + 1);
            return null;
        }

        String fullPath = String.format("/%s%s/%d/%d/", apiKey, endpoint, startIndex, endIndex);
        String fullUrl = apiUrl + fullPath;

        log.info("따릉이 API 호출: {} ({}건)", fullUrl, endIndex - startIndex + 1);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(apiUrl)
                    .build();

            DdareungiApiResponseDto response = webClient.get()
                    .uri(fullPath)
                    .retrieve()
                    .bodyToMono(DdareungiApiResponseDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response == null || response.getRentBikeStatus() == null) {
                log.warn("따릉이 API 응답이 null이거나 비어있습니다.");
                return null;
            }

            if (response.getRentBikeStatus().getResult() != null) {
                String code = response.getRentBikeStatus().getResult().getCode();
                String message = response.getRentBikeStatus().getResult().getMessage();
                
                // INFO-000: 정상 처리
                if ("INFO-000".equals(code)) {
                    // 정상 처리
                } else if ("INFO-200".equals(code)) {
                    // 해당하는 데이터가 없음
                    log.warn("따릉이 API: 해당하는 데이터가 없습니다. ({} - {})", code, message);
                    return null;
                } else {
                    // 기타 오류 (ERROR-300, ERROR-301, ERROR-310, ERROR-331~336, ERROR-500, ERROR-600, ERROR-601 등)
                    log.error("따릉이 API 오류: {} - {}", code, message);
                    return null;
                }
            }

            log.info("따릉이 API 호출 성공: {}개 대여소 정보 수신", 
                    response.getRentBikeStatus().getRow() != null ? 
                    response.getRentBikeStatus().getRow().size() : 0);

            return response;
        } catch (WebClientResponseException e) {
            log.error("따릉이 API 호출 실패: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return null;
        } catch (Exception e) {
            log.error("따릉이 API 호출 중 예외 발생", e);
            return null;
        }
    }

    public DdareungiApiResponseDto fetchAllStationData() {
        // 먼저 전체 개수를 확인하기 위해 1개만 가져옴
        DdareungiApiResponseDto firstResponse = fetchStationData(1, 1);
        
        if (firstResponse == null || 
            firstResponse.getRentBikeStatus() == null ||
            firstResponse.getRentBikeStatus().getListTotalCount() == null) {
            log.warn("따릉이 API에서 전체 개수를 확인할 수 없습니다.");
            return null;
        }

        int totalCount = firstResponse.getRentBikeStatus().getListTotalCount();
        log.info("따릉이 전체 대여소 개수: {}", totalCount);

        // 1,000건 이하면 한 번에 가져오기
        if (totalCount <= 1000) {
            return fetchStationData(1, totalCount);
        }

        // 1,000건 초과 시 분할 호출 (예: 1/1,000, 1001/2,000)
        log.info("대여소 개수가 1,000건을 초과하여 분할 호출을 진행합니다.");
        
        // 첫 번째 페이지 가져오기 (1~1000)
        DdareungiApiResponseDto mergedResponse = fetchStationData(1, Math.min(1000, totalCount));
        
        if (mergedResponse == null || 
            mergedResponse.getRentBikeStatus() == null ||
            mergedResponse.getRentBikeStatus().getRow() == null) {
            log.error("따릉이 API 첫 번째 페이지 호출 실패");
            return null;
        }

        int pageSize = 1000;
        int currentStart = 1001; // 두 번째 페이지부터 시작
        
        while (currentStart <= totalCount) {
            int currentEnd = Math.min(currentStart + pageSize - 1, totalCount);
            log.info("따릉이 API 분할 호출: {} ~ {} (전체: {})", currentStart, currentEnd, totalCount);
            
            DdareungiApiResponseDto pageResponse = fetchStationData(currentStart, currentEnd);
            
            if (pageResponse != null && 
                pageResponse.getRentBikeStatus() != null &&
                pageResponse.getRentBikeStatus().getRow() != null) {
                
                // 기존 데이터에 추가
                mergedResponse.getRentBikeStatus().getRow().addAll(
                    pageResponse.getRentBikeStatus().getRow()
                );
            }
            
            currentStart = currentEnd + 1;
        }

        log.info("따릉이 API 전체 데이터 수집 완료: 총 {}개 대여소", 
                mergedResponse.getRentBikeStatus().getRow() != null ? 
                mergedResponse.getRentBikeStatus().getRow().size() : 0);

        return mergedResponse;
    }
}

