package com.example.repository;

import com.example.dox.ProcessScore;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProcessScoreRepository extends ReactiveCrudRepository<ProcessScore,String> {
    @Query("select * from process_score ps,teacher t where t.group_id=:gid and " +
            "t.id=ps.teacher_id and ps.process_id=:pid")
    Flux<ProcessScore> getProcessScoreByPidAndGid(@Param("gid") int gid, @Param("pid") String pid);
}
