package com.example.repository;

import com.example.dox.ProcessScore;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessScoreRepository extends ReactiveCrudRepository<ProcessScore,String> {
}
