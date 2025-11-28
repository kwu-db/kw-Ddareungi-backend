package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.dto.request.RequestComment;

public interface CommentCommandService {
    Long writeCommentAtBoard(Long boardId, String username, RequestComment requestComment);
}
