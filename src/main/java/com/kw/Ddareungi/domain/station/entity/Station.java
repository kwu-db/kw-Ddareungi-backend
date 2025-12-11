package com.kw.Ddareungi.domain.station.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Station extends BaseTimeEntity {
    private Long id;
    private String stationName;
    private Double latitude;
    private Double longitude;
    private String address;
    private int capacity;
    private int availableBikes;
    private LocalDate installationDate;
    private LocalTime closedDate;
    private Long createdById;
    private Long modifiedById;
}
