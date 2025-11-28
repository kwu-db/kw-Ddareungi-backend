package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardQueryService {
    
    // 게시글 단일 조회
    BoardResponseDto.BoardInfo getBoard(Long boardId);
    
    // 게시글 목록 조회 (타입별)
    Page<BoardResponseDto.BoardListInfo> getBoardsByType(Board.BoardType boardType, Pageable pageable);
    
    // 게시글 목록 조회 (전체)
    Page<BoardResponseDto.BoardListInfo> getAllBoards(Pageable pageable);
    
    // 게시글 검색
    Page<BoardResponseDto.BoardListInfo> searchBoards(String keyword, Pageable pageable);
}
