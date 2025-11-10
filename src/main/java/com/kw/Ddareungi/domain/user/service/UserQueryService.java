package com.kw.Ddareungi.domain.user.service;


import com.kw.Ddareungi.domain.user.entity.User;

public interface UserQueryService {
    User getByLoginId(String loginId);

    User getUserByUsername(String username);
}
