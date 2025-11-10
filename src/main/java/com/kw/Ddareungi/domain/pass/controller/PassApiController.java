package com.kw.Ddareungi.domain.pass.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[유저 페이지]이용권 API")
@RestController
@RequestMapping("/api/v1/passes")
@RequiredArgsConstructor
public class PassApiController {
}
