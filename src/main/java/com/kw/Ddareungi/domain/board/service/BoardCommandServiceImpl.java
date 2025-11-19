package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardRequestDto;
import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandServiceImpl implements BoardCommandService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public BoardResponseDto.BoardInfo createBoard(String username, BoardRequestDto.CreateBoard request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String sql = """
                INSERT INTO board (user_id, board_type, title, content, created_date, last_modified_date)
                VALUES (:userId, :boardType, :title, :content, :createdDate, :lastModifiedDate)
                """;

        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("boardType", request.getBoardType().name())
                .addValue("title", request.getTitle())
                .addValue("content", request.getContent())
                .addValue("createdDate", now)
                .addValue("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"board_id"});
        Long boardId = Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("게시글 저장 중 문제가 발생했습니다."));

        return fetchBoardInfo(boardId);
    }

    @Override
    public BoardResponseDto.BoardInfo updateBoard(Long boardId, String username, BoardRequestDto.UpdateBoard request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long writerId = fetchBoardOwnerId(boardId);
        if (!writerId.equals(user.getId())) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        List<String> sets = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("boardId", boardId);

        if (StringUtils.hasText(request.getTitle())) {
            sets.add("title = :title");
            params.addValue("title", request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            sets.add("content = :content");
            params.addValue("content", request.getContent());
        }

        if (!CollectionUtils.isEmpty(sets)) {
            sets.add("last_modified_date = :lastModifiedDate");
            params.addValue("lastModifiedDate", LocalDateTime.now());
            String updateSql = "UPDATE board SET " + String.join(", ", sets) + " WHERE board_id = :boardId";
            jdbcTemplate.update(updateSql, params);
        }

        return fetchBoardInfo(boardId);
    }

    @Override
    public void deleteBoard(Long boardId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long writerId = fetchBoardOwnerId(boardId);
        if (!writerId.equals(user.getId())) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        String sql = "DELETE FROM board WHERE board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    private BoardResponseDto.BoardInfo fetchBoardInfo(Long boardId) {
        try {
            return jdbcTemplate.queryForObject(BoardSqlSupport.BOARD_DETAIL_SQL, new MapSqlParameterSource("boardId", boardId), BoardSqlSupport.BOARD_INFO_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }

    private Long fetchBoardOwnerId(Long boardId) {
        String sql = "SELECT user_id FROM board WHERE board_id = :boardId";
        try {
            return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("boardId", boardId), Long.class);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }
}
