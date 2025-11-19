package com.kw.Ddareungi.domain.board.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Board extends BaseTimeEntity {

    private Long id;
    private Long userId;
    private BoardType boardType;
    private String title;
    private String content;

    public enum BoardType {
        QNA, NOTICE, REPORT
    }

    // 비즈니스 메서드들
    public void updateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        this.title = title;
    }

    public void updateContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("내용은 null일 수 없습니다.");
        }
        this.content = content;
    }

    public void updateBoard(String title, String content) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }
}
