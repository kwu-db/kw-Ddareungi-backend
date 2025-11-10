package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;
}
