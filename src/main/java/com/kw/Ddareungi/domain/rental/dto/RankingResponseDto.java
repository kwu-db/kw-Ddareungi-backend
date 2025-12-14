package com.kw.Ddareungi.domain.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseDto {
    private Long rank;
    private Long userId;
    private String userName;
    private Long value; // 이용횟수 또는 이용시간(초)
    
    // 이용시간 랭킹용 필드 (시분초)
    private Long hours;
    private Long minutes;
    private Long seconds;
}

