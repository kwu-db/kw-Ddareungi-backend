package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.dto.request.ResponseCommentList;
import com.kw.Ddareungi.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;


    /**
     * SQL 입력
     * 1. boardId를 통해 댓글 목록 조회
     * 2. return에 맞게 매핑
     * @param boardId
     * @return ResponseCommentList
     */
    @Override
    public ResponseCommentList getCommentList(Long boardId) {
        //1. query sql
        //2. convert to dto
        return null;
    }
}
