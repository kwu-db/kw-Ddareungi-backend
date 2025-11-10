package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.dto.request.ResponseCommentList;

public interface CommentQueryService {
    ResponseCommentList getCommentList(Long boardId);

}
