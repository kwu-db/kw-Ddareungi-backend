package com.kw.Ddareungi.domain.pass.service;

import com.kw.Ddareungi.domain.pass.dto.ResponsePassList;
import com.kw.Ddareungi.domain.pass.dto.ResponseUserPassList;

public interface PassQueryService {
    ResponsePassList getPassList();

    ResponseUserPassList getUserPassList(String username);
}
