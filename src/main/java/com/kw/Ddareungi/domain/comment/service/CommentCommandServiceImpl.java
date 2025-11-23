package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.dto.request.RequestComment;
import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public Long writeCommentAtBoard(Long boardId, String username, RequestComment requestComment) {
        validateBoardExists(boardId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String sql = """
                INSERT INTO comment (board_id, user_id, content, created_date, last_modified_date)
                VALUES (:boardId, :userId, :content, :createdDate, :lastModifiedDate)
                """;
        LocalDateTime now = LocalDateTime.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("boardId", boardId)
                .addValue("userId", user.getId())
                .addValue("content", requestComment.getContent())
                .addValue("createdDate", now)
                .addValue("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"comment_id"});
        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("댓글 저장 중 오류가 발생했습니다."));
    }

    private void validateBoardExists(Long boardId) {
        String sql = "SELECT COUNT(*) FROM board WHERE board_id = :boardId";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("boardId", boardId), Long.class);
        if (count == null || count == 0L) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }
}
