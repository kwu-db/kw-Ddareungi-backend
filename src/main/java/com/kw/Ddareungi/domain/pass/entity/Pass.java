package com.kw.Ddareungi.domain.pass.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Pass extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pass_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PassType passType;

    @Column(nullable = false)
    private Integer price;
}
