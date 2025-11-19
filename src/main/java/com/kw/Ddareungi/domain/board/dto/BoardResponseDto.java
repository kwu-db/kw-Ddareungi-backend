package com.kw.Ddareungi.domain.board.dto;

import com.kw.Ddareungi.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BoardResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardInfo {
        private Long boardId;
        private Long userId;
        private String userName;
        private Board.BoardType boardType;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private LocalDateTime lastModifiedDate;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardListInfo {
        private Long boardId;
        private Long userId;
        private String userName;
        private Board.BoardType boardType;
        private String title;
        private LocalDateTime createdDate;

    }
}
