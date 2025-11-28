package com.kw.Ddareungi.domain.pass.controller;

import com.kw.Ddareungi.api.common.dto.ApiResponseDto;
import com.kw.Ddareungi.domain.pass.dto.ResponsePassList;
import com.kw.Ddareungi.domain.pass.dto.ResponseUserPassList;
import com.kw.Ddareungi.domain.pass.service.PassCommandService;
import com.kw.Ddareungi.domain.pass.service.PassQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이용권 API")
@RestController
@RequestMapping("/api/v1/passes")
@RequiredArgsConstructor
public class PassApiController {

    private final PassCommandService passCommandService;
    private final PassQueryService passQueryService;
    //TODO 이용권 PostInit

    @Operation(summary = "이용권 조회")
    @GetMapping
    public ApiResponseDto<ResponsePassList> getPassList() {
        return ApiResponseDto.onSuccess(passQueryService.getPassList());
    }

    @Operation(summary = "내가 구매한 이용권 조회")
    @GetMapping("/users")       //api endpoint 다시
    public ApiResponseDto<ResponseUserPassList> getUserPassList(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ApiResponseDto.onSuccess(passQueryService.getUserPassList(username));
    }

    @Operation(summary = "이용권 구매하기")
    @PostMapping("/{passId}")
    public ApiResponseDto<Long> buyPass(@PathVariable Long passId,
                                        @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ApiResponseDto.onSuccess(passCommandService.buyPass(passId, username));
    }
}
