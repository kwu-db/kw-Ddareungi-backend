package com.kw.Ddareungi.domain.pass.dto;

import com.kw.Ddareungi.domain.pass.entity.PassType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponsePass {
    private Long passId;
    private PassType passType;
    private int price;
}
