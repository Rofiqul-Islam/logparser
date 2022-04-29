package com.cloudhubs.logparser.repository;

import com.cloudhubs.logparser.model.LogModelDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Md Rofiqul Islam
 */
@Repository
public interface ModelRepository extends JpaRepository<LogModelDAO, Long> {
    LogModelDAO findById(long id);
}