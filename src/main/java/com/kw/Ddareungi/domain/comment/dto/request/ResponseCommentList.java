package com.kw.Ddareungi.domain.comment.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ResponseCommentList {
    @Builder.Default
    List<ResponseComment> commentList = new ArrayList<>();
}
