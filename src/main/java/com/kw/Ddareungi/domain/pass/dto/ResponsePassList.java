package com.kw.Ddareungi.domain.pass.dto;

import com.kw.Ddareungi.domain.comment.dto.request.ResponseComment;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ResponsePassList {
    @Builder.Default
    private List<ResponsePass> passes = new ArrayList<>();
}
