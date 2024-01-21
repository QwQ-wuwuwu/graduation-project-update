package com.example.repository;

import com.example.dox.File;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ReactiveCrudRepository<File,String> {
}
