package com.kw.Ddareungi.domain.pass.service;

import com.kw.Ddareungi.domain.pass.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PassCommandServiceImpl implements PassCommandService {
    private final PassRepository passRepository;


    /**
     * SQL 입력
     * 1. passId와 username 존재 유무 확인(Exist)
     * 2. argument로 create User_Pass
     * 3. condition 1: price counting <- 사용자가 가지고 있는 credit 차감 필요
     * 3-1. 필요하다면 별도의 SQL 작성
     *
     * @param passId
     * @param username
     * @return
     */
    @Override
    public Long buyPass(Long passId, String username) {

        return 0L;
    }
}
