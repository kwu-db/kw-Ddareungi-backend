package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BoardSqlSupport {

    public static final String BOARD_DETAIL_SQL = """
            SELECT b.board_id,
                   b.board_type,
                   b.title,
                   b.content,
                   b.created_date,
                   b.last_modified_date,
                   u.user_id,
                   u.name   AS user_name
              FROM board b
              JOIN users u ON u.user_id = b.user_id
             WHERE b.board_id = :boardId
            """;

    public static final String BOARD_LIST_BASE_SQL = """
            SELECT b.board_id,
                   b.board_type,
                   b.title,
                   b.created_date,
                   u.user_id,
                   u.name AS user_name
              FROM board b
              JOIN users u ON u.user_id = b.user_id
            """;

    public static final RowMapper<BoardResponseDto.BoardInfo> BOARD_INFO_MAPPER = new RowMapper<>() {
        @Override
        public BoardResponseDto.BoardInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp created = rs.getTimestamp("created_date");
            Timestamp modified = rs.getTimestamp("last_modified_date");
            return BoardResponseDto.BoardInfo.builder()
                    .boardId(rs.getLong("board_id"))
                    .userId(rs.getLong("user_id"))
                    .userName(rs.getString("user_name"))
                    .boardType(Board.BoardType.valueOf(rs.getString("board_type")))
                    .title(rs.getString("title"))
                    .content(rs.getString("content"))
                    .createdDate(created != null ? created.toLocalDateTime() : null)
                    .lastModifiedDate(modified != null ? modified.toLocalDateTime() : null)
                    .build();
        }
    };

    public static final RowMapper<BoardResponseDto.BoardListInfo> BOARD_LIST_MAPPER = new RowMapper<>() {
        @Override
        public BoardResponseDto.BoardListInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp created = rs.getTimestamp("created_date");
            return BoardResponseDto.BoardListInfo.builder()
                    .boardId(rs.getLong("board_id"))
                    .userId(rs.getLong("user_id"))
                    .userName(rs.getString("user_name"))
                    .boardType(Board.BoardType.valueOf(rs.getString("board_type")))
                    .title(rs.getString("title"))
                    .createdDate(created != null ? created.toLocalDateTime() : null)
                    .build();
        }
    };
}

