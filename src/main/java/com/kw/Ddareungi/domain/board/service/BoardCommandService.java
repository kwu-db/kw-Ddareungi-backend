package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardRequestDto;
import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;

public interface BoardCommandService {
    
    // 게시글 작성
    BoardResponseDto.BoardInfo createBoard(String username, BoardRequestDto.CreateBoard request);
    
    // 게시글 수정
    BoardResponseDto.BoardInfo updateBoard(Long boardId, String username, BoardRequestDto.UpdateBoard request);
    
    // 게시글 삭제
    void deleteBoard(Long boardId, String username);
}
