package com.kw.Ddareungi.domain.rental.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[유저 페이지]대여 API")
@RestController
@RequestMapping("/api/v1/users/rentals")
@RequiredArgsConstructor
public class RentalApiController {
}
