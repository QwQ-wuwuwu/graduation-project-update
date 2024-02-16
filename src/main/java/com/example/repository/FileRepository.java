package com.example.repository;

import com.example.dox.File;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FileRepository extends ReactiveCrudRepository<File,String> {
    @Query("select * from file f where f.process_id=:pid")
    Flux<File> getFilesByPid(@Param("pid") String pid);
}
