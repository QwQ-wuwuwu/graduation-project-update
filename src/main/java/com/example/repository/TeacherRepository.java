package com.example.repository;

import com.example.dox.Teacher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends ReactiveCrudRepository<Teacher,String> {
}
