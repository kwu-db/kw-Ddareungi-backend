package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardRequestDto;
import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import com.kw.Ddareungi.domain.board.repository.BoardRepository;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandServiceImpl implements BoardCommandService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    public BoardResponseDto.BoardInfo createBoard(Long userId, BoardRequestDto.CreateBoard request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Board board = Board.builder()
                .user(user)
                .boardType(request.getBoardType())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Board savedBoard = boardRepository.save(board);
        return BoardResponseDto.BoardInfo.from(savedBoard);
    }

    @Override
    public BoardResponseDto.BoardInfo updateBoard(Long boardId, Long userId, BoardRequestDto.UpdateBoard request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }

        // 수정할 필드가 있는 경우에만 업데이트
        board.updateBoard(request.getTitle(), request.getContent());

        Board updatedBoard = boardRepository.save(board);
        return BoardResponseDto.BoardInfo.from(updatedBoard);
    }

    @Override
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }
}
