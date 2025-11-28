package com.kw.Ddareungi.domain.comment.service;

import com.kw.Ddareungi.domain.comment.dto.request.ResponseComment;
import com.kw.Ddareungi.domain.comment.dto.request.ResponseCommentList;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<ResponseComment> COMMENT_ROW_MAPPER = new RowMapper<>() {
        @Override
        public ResponseComment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ResponseComment.builder()
                    .commentId(rs.getLong("comment_id"))
                    .writerName(rs.getString("writer_name"))
                    .content(rs.getString("content"))
                    .build();
        }
    };

    @Override
    public ResponseCommentList getCommentList(Long boardId) {
        String sql = """
                SELECT c.comment_id,
                       c.content,
                       c.created_date,
                       u.name AS writer_name
                  FROM comment c
                  JOIN users u ON u.user_id = c.user_id
                 WHERE c.board_id = :boardId
                 ORDER BY c.created_date ASC
                """;
        List<ResponseComment> comments = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("boardId", boardId),
                COMMENT_ROW_MAPPER
        );
        return ResponseCommentList.builder()
                .commentList(comments)
                .build();
    }
}
