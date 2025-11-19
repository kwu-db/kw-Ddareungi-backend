package com.kw.Ddareungi.domain.pass.dto;

import com.kw.Ddareungi.domain.pass.entity.PassType;
import com.kw.Ddareungi.domain.pass.entity.UserPassStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ResponseUserPass {
    private Long userPassId;
    private Long passId;
    private PassType passType;
    private int price;
    private UserPassStatus status;
    private LocalDate activatedDate;
    private LocalDate expiredDate;
}
