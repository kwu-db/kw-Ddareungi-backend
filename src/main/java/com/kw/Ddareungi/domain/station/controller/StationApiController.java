package com.kw.Ddareungi.domain.station.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대여소 API")
@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationApiController {
}
