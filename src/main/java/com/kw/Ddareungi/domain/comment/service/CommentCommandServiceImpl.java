package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.dto.request.RequestComment;
import com.kw.Ddareungi.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;

    /**
     * SQL 입력
     * 1. boardId와 username을 통해 Board, User 존재 유무 확인(Exist)
     * 2. argument에 맞게 데이터 입력
     * @param boardId
     * @param username
     * @param requestComment
     * @return
     */
    @Override
    public Long writeCommentAtBoard(Long boardId, String username, RequestComment requestComment) {
        return 0L;
    }
}
