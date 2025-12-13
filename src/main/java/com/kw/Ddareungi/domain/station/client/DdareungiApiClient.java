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
        if (endIndex - startIndex + 1 > 1000) {
            log.error("따릉이 API 호출 실패: 한 번에 최대 1,000건을 초과할 수 없습니다. (요청: {}건)", endIndex - startIndex + 1);
            return null;
        }

        String fullPath = String.format("/%s%s/%d/%d/", apiKey, endpoint, startIndex, endIndex);

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
                
                if ("INFO-000".equals(code)) {
                } else if ("INFO-200".equals(code)) {
                    log.warn("따릉이 API: 해당하는 데이터가 없습니다. ({} - {})", code, message);
                    return null;
                } else {
                    log.error("따릉이 API 오류: {} - {}", code, message);
                    return null;
                }
            }

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
        DdareungiApiResponseDto firstResponse = fetchStationData(1, 1);
        
        if (firstResponse == null || 
            firstResponse.getRentBikeStatus() == null) {
            log.warn("따릉이 API에서 응답을 받을 수 없습니다.");
            return null;
        }

        Integer listTotalCount = firstResponse.getRentBikeStatus().getListTotalCount();
        
        if (listTotalCount == null || listTotalCount == 0 || listTotalCount == 1) {
            firstResponse = fetchStationData(1, 1000);
            
            if (firstResponse == null || 
                firstResponse.getRentBikeStatus() == null) {
                log.error("따릉이 API에서 응답을 받을 수 없습니다.");
                return null;
            }
            
            listTotalCount = firstResponse.getRentBikeStatus().getListTotalCount();
        }
        
        if (listTotalCount != null && listTotalCount > 0 && listTotalCount < 1000) {
            return fetchStationData(1, listTotalCount);
        }
        
        DdareungiApiResponseDto mergedResponse;
        if (firstResponse.getRentBikeStatus().getRow() != null && 
            firstResponse.getRentBikeStatus().getRow().size() == 1) {
            mergedResponse = fetchStationData(1, 1000);
        } else {
            mergedResponse = firstResponse;
        }
        
        if (mergedResponse == null || 
            mergedResponse.getRentBikeStatus() == null ||
            mergedResponse.getRentBikeStatus().getRow() == null) {
            log.error("따릉이 API 첫 번째 페이지 호출 실패");
            return null;
        }

        int pageSize = 1000;
        int currentStart = 1001;
        boolean hasMoreData = true;
        int totalFetched = mergedResponse.getRentBikeStatus().getRow().size();
        Integer lastListTotalCount = mergedResponse.getRentBikeStatus().getListTotalCount();
        
        while (hasMoreData) {
            int currentEnd = currentStart + pageSize - 1;
            DdareungiApiResponseDto pageResponse = fetchStationData(currentStart, currentEnd);
            
            if (pageResponse == null) {
                hasMoreData = false;
                break;
            }
            
            if (pageResponse.getRentBikeStatus() == null ||
                pageResponse.getRentBikeStatus().getRow() == null ||
                pageResponse.getRentBikeStatus().getRow().isEmpty()) {
                hasMoreData = false;
                break;
            }
            
            Integer currentListTotalCount = pageResponse.getRentBikeStatus().getListTotalCount();
            if (currentListTotalCount != null && !currentListTotalCount.equals(lastListTotalCount)) {
                lastListTotalCount = currentListTotalCount;
            }
            
            int receivedCount = pageResponse.getRentBikeStatus().getRow().size();
            mergedResponse.getRentBikeStatus().getRow().addAll(
                pageResponse.getRentBikeStatus().getRow()
            );
            
            totalFetched += receivedCount;
            
            if (receivedCount < pageSize) {
                if (lastListTotalCount != null && totalFetched < lastListTotalCount) {
                    currentStart = currentEnd + 1;
                } else {
                    hasMoreData = false;
                }
            } else {
                currentStart = currentEnd + 1;
            }
        }
        
        log.info("따릉이 API 동기화 완료: 총 {}개 대여소", totalFetched);

        return mergedResponse;
    }
}

