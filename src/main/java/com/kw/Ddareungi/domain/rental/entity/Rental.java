package com.kw.Ddareungi.domain.rental.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rental extends BaseTimeEntity {
    private Long id;
    private String bikeNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;
    private Long startStationId;
    private Long endStationId;
}
