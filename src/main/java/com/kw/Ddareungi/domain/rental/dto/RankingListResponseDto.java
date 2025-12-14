package com.kw.Ddareungi.domain.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingListResponseDto {
    @Builder.Default
    private List<RankingResponseDto> rankings = new ArrayList<>();
}

