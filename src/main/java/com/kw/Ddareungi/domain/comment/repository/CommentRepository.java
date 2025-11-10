package com.kw.Ddareungi.domain.comment.repository;

import com.kw.Ddareungi.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
