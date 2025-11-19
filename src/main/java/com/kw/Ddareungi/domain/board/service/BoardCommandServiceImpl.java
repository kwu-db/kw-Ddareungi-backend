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

    /**
     * SQL 작성
     * 1. username Exist 확인
     * 2. 아래 builder와 같이 insert
     * 3. return Long (현재 리턴값 변경 필요)
     * @param username
     * @param request
     * @return BoardId
     */
    @Override
    public BoardResponseDto.BoardInfo createBoard(String username, BoardRequestDto.CreateBoard request) {

        User user = userRepository.findByUsername(username)
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

    /**
     * SQL 작성
     * 1. boardId를 통해 변경할 board 설정
     * 2. username으로 validation
     * 3. title, content 수정
     * 4. return Long (현재 리턴값 변경 필요)
     * @param boardId
     * @param username
     * @param request
     * @return
     */
    @Override
    public BoardResponseDto.BoardInfo updateBoard(Long boardId, String username, BoardRequestDto.UpdateBoard request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!board.getUser().getUsername().equals(username)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }

        // 수정할 필드가 있는 경우에만 업데이트
        board.updateBoard(request.getTitle(), request.getContent());

        Board updatedBoard = boardRepository.save(board);
        return BoardResponseDto.BoardInfo.from(updatedBoard);
    }

    /**
     * SQL 작성
     * 1. boardId로 삭제 board 설정
     * 2. username으로 validation
     * 3. 삭제
     * @param boardId
     * @param username
     */
    @Override
    public void deleteBoard(Long boardId, String username) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!board.getUser().getUsername().equals(username)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }
}
