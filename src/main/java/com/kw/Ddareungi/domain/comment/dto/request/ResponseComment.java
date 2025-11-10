package com.kw.Ddareungi.domain.comment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseComment {
    private Long commentId;
    private String writerName;
    private String content;
}
