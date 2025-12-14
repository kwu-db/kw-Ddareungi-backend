package com.kw.Ddareungi.domain.pass.repository;

import com.kw.Ddareungi.domain.pass.entity.Pass;

import java.util.List;
import java.util.Optional;

public interface PassRepository {
    Optional<Pass> findById(Long passId);
    List<Pass> findAll();
    Long save(Pass pass);
    boolean existsById(Long passId);
}

