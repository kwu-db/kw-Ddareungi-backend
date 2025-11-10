package com.kw.Ddareungi.domain.rental.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import com.kw.Ddareungi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rental extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long id;

    private String bikeNumber;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "station_id")
//    private Station startStation;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "station_id")
//    private Station endStation;
}
