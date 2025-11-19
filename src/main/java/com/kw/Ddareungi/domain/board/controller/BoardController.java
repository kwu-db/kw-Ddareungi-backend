package com.kw.Ddareungi.domain.board.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.board.dto.BoardRequestDto;
import com.kw.Ddareungi.domain.board.dto.BoardResponseDto;
import com.kw.Ddareungi.domain.board.entity.Board;
import com.kw.Ddareungi.domain.board.service.BoardCommandService;
import com.kw.Ddareungi.domain.board.service.BoardQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardCommandService boardCommandService;
    private final BoardQueryService boardQueryService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponseDto<BoardResponseDto.BoardInfo>> createBoard(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody BoardRequestDto.CreateBoard request) {
        
        BoardResponseDto.BoardInfo response = boardCommandService.createBoard(username, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PatchMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto<BoardResponseDto.BoardInfo>> updateBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId,
            @AuthenticationPrincipal String username,
            @Valid @RequestBody BoardRequestDto.UpdateBoard request) {
        
        BoardResponseDto.BoardInfo response = boardCommandService.updateBoard(boardId, username, request);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId,
            @AuthenticationPrincipal String username) {
        
        boardCommandService.deleteBoard(boardId, username);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(null));
    }

    @Operation(summary = "게시글 단일 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto<BoardResponseDto.BoardInfo>> getBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId) {
        
        BoardResponseDto.BoardInfo response = boardQueryService.getBoard(boardId);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<BoardResponseDto.BoardListInfo>>> getBoards(
            @Parameter(description = "게시글 타입") @RequestParam(required = false) Board.BoardType boardType,
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<BoardResponseDto.BoardListInfo> response;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            response = boardQueryService.searchBoards(keyword, pageable);
        } else if (boardType != null) {
            response = boardQueryService.getBoardsByType(boardType, pageable);
        } else {
            response = boardQueryService.getAllBoards(pageable);
        }
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }
}
