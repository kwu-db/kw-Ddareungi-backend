package com.kw.Ddareungi.domain.pass.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ResponseUserPassList {
    @Builder.Default
    private List<ResponseUserPass> userPasses = new ArrayList<>();
}
