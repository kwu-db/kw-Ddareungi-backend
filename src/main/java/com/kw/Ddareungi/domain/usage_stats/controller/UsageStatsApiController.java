package com.kw.Ddareungi.domain.usage_stats.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[관리자 페이지]통계 API")
@RestController
@RequestMapping("/api/v1/admin/usage-stats")
@RequiredArgsConstructor
public class UsageStatsApiController {
}
