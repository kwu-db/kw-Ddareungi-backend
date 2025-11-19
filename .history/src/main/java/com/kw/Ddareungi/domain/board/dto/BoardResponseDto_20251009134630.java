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

        public static BoardInfo from(Board board) {
            return BoardInfo.builder()
                    .boardId(board.getId())
                    .userId(board.getUser().getId())
                    .userName(board.getUser().getName())
                    .boardType(board.getBoardType())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .createdDate(board.getCreatedDate())
                    .lastModifiedDate(board.getLastModifiedDate())
                    .build();
        }
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

        public static BoardListInfo from(Board board) {
            return BoardListInfo.builder()
                    .boardId(board.getId())
                    .userId(board.getUser().getId())
                    .userName(board.getUser().getName())
                    .boardType(board.getBoardType())
                    .title(board.getTitle())
                    .createdDate(board.getCreatedDate())
                    .build();
        }
    }
}
