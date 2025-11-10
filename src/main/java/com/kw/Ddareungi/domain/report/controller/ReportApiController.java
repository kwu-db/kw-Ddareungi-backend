package com.kw.Ddareungi.domain.report.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[유저 페이지]리포트(제보) API")
@RestController
@RequestMapping("/api/v1/users/reports")
@RequiredArgsConstructor
public class ReportApiController {
}
