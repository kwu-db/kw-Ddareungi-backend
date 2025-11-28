package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import com.kw.Ddareungi.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryServiceImpl implements BoardQueryService {

    private final BoardRepository boardRepository;

    @Override
    public BoardResponseDto.BoardInfo getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        return BoardResponseDto.BoardInfo.from(board);
    }

    @Override
    public Page<BoardResponseDto.BoardListInfo> getBoardsByType(Board.BoardType boardType, Pageable pageable) {
        return boardRepository.findByBoardType(boardType, pageable)
                .map(BoardResponseDto.BoardListInfo::from);
    }

    @Override
    public Page<BoardResponseDto.BoardListInfo> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(BoardResponseDto.BoardListInfo::from);
    }

    @Override
    public Page<BoardResponseDto.BoardListInfo> searchBoards(String keyword, Pageable pageable) {
        return boardRepository.findByTitleOrContentContaining(keyword, pageable)
                .map(BoardResponseDto.BoardListInfo::from);
    }
}
