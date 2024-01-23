package com.example.repository;

import com.example.dox.Process;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProcessRepository extends ReactiveCrudRepository<Process,String> {
    @Modifying
    @Query("delete from process p where p.id=:pid")
    Mono<Boolean> deleteProcessBy(@Param("pid") String pid);
}
