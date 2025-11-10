package com.kw.Ddareungi.domain.usage_stats.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import com.kw.Ddareungi.domain.rental.entity.Rental;
import com.kw.Ddareungi.domain.station.entity.Station;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UsageStats extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_stats_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    private Rental rental;
    //?? 추가해야할 지표 생각해보기
}
