package com.kw.Ddareungi.domain.usage_stats.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UsageStats extends BaseTimeEntity {
    private Long id;
    private Long stationId;
    private Long rentalId;
    // TODO: add aggregated metrics when requirements are clarified
}
