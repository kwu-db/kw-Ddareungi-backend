package com.kw.Ddareungi.domain.comment.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.comment.dto.request.RequestComment;
import com.kw.Ddareungi.domain.comment.dto.request.ResponseCommentList;
import com.kw.Ddareungi.domain.comment.service.CommentCommandService;
import com.kw.Ddareungi.domain.comment.service.CommentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;


    @Operation(summary = "댓글 작성")
    @PostMapping("/boards/{boardId}")
    public ApiResponseDto<Long> commentAtBoard(@PathVariable Long boardId,
                                               @RequestBody RequestComment requestComment,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ApiResponseDto.onSuccess(commentCommandService.writeCommentAtBoard(boardId, username, requestComment));
    }

    // pagination 고려
    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/boards/{boardId}")
    public ApiResponseDto<ResponseCommentList> getCommentList(@PathVariable Long boardId) {
        return ApiResponseDto.onSuccess(commentQueryService.getCommentList(boardId));
    }
}
