package com.kw.Ddareungi.domain.board.repository;

import com.kw.Ddareungi.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    
    // 게시글 타입별 조회
    Page<Board> findByBoardType(Board.BoardType boardType, Pageable pageable);
    
    // 사용자별 게시글 조회
    List<Board> findByUserId(Long userId);
    
    // 제목으로 검색
    Page<Board> findByTitleContaining(String title, Pageable pageable);
    
    // 내용으로 검색
    Page<Board> findByContentContaining(String content, Pageable pageable);
    
    // 제목 또는 내용으로 검색
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
}
