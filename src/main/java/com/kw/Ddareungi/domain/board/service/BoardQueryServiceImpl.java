package com.kw.Ddareungi.domain.board.service;

import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryServiceImpl implements BoardQueryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public BoardResponseDto.BoardInfo getBoard(Long boardId) {
        try {
            return jdbcTemplate.queryForObject(
                    BoardSqlSupport.BOARD_DETAIL_SQL,
                    new MapSqlParameterSource("boardId", boardId),
                    BoardSqlSupport.BOARD_INFO_MAPPER
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }

    @Override
    public Page<BoardResponseDto.BoardListInfo> getBoardsByType(Board.BoardType boardType, Pageable pageable) {
        String condition = " WHERE b.board_type = :boardType";
        MapSqlParameterSource params = new MapSqlParameterSource("boardType", boardType.name());
        return queryBoardPage(condition, params, pageable);
    }

    @Override
    public Page<BoardResponseDto.BoardListInfo> getAllBoards(Pageable pageable) {
        return queryBoardPage("", new MapSqlParameterSource(), pageable);
    }

    @Override
    public Page<BoardResponseDto.BoardListInfo> searchBoards(String keyword, Pageable pageable) {
        String condition = " WHERE (b.title LIKE :keyword OR b.content LIKE :keyword)";
        MapSqlParameterSource params = new MapSqlParameterSource("keyword", "%" + keyword + "%");
        return queryBoardPage(condition, params, pageable);
    }

    private Page<BoardResponseDto.BoardListInfo> queryBoardPage(String condition,
                                                                MapSqlParameterSource params,
                                                                Pageable pageable) {
        String countSql = "SELECT COUNT(*) FROM board b" + condition;
        long total = jdbcTemplate.queryForObject(countSql, params, Long.class);

        MapSqlParameterSource pagingParams = new MapSqlParameterSource();
        params.getValues().forEach(pagingParams::addValue);
        pagingParams
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        String dataSql = BoardSqlSupport.BOARD_LIST_BASE_SQL + condition
                + " ORDER BY b.created_date DESC LIMIT :limit OFFSET :offset";
        List<BoardResponseDto.BoardListInfo> content =
                jdbcTemplate.query(dataSql, pagingParams, BoardSqlSupport.BOARD_LIST_MAPPER);

        return new PageImpl<>(content, pageable, total);
    }
}
