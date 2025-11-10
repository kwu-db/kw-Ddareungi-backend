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


    @Override
    public Long buyPass(Long passId, String username) {
        /**
         * create user_pass sql
         * condition 1: price counting
         */
        return 0L;
    }
}
