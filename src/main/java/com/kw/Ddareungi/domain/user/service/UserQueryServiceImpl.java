package com.kw.Ddareungi.domain.user.service;


import com.kw.Ddareungi.domain.user.entity.User;
import com.kw.Ddareungi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    /**
     * SQL 입력
     * 1. loginId에 부합하는 User 찾기
     * 2. return USER
     * @param loginId
     * @return
     */
    @Override
    public User getByLoginId(String loginId) {
        //user 로그인 sql
        return User.builder().build();
    }

    /**
     * SQL 입력
     * 1. username을 통해 USer 찾기
     * 2. return User
     * @param username
     * @return
     */
    @Override
    public User getUserByUsername(String username) {

        //username으로 select sql
        return null;
    }
}
