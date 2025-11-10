package com.kw.Ddareungi.domain.comment.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API")
@RestController
@RequestMapping("/api/v1/users/comments")
@RequiredArgsConstructor
public class CommentApiController {

    @PostMapping("/api/v1/comments/boards/{boardId}")
    public ApiResponseDto<Long> commentAtBoard(@PathVariable Long boardId){};
//                                               @RequestBody RequestComment,)
}
