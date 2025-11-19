package com.kw.Ddareungi.domain.pass.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserPass extends BaseTimeEntity {
    private Long id;
    private Long userId;
    private Long passId;
    private LocalDate activatedDate;
    private LocalDate expiredDate;
    private UserPassStatus userPassStatus;
}
