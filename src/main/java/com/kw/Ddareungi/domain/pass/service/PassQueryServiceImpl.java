package com.kw.Ddareungi.domain.pass.service;

import com.kw.Ddareungi.domain.pass.dto.ResponsePassList;
import com.kw.Ddareungi.domain.pass.dto.ResponseUserPassList;
import com.kw.Ddareungi.domain.pass.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassQueryServiceImpl implements PassQueryService {
    private final PassRepository passRepository;

    @Override
    public ResponsePassList getPassList() {
        //query all pass
        return null;
    }

    @Override
    public ResponseUserPassList getUserPassList(String username) {
        //query pass and join user on user.username=:username
        return null;
    }
}
