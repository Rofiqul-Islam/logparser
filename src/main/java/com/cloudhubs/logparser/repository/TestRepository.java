package com.cloudhubs.logparser.repository;

import com.cloudhubs.logparser.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Test findById(long id);
}