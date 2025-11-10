package com.kw.Ddareungi.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {
}
