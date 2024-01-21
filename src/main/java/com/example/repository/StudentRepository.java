package com.example.repository;

import com.example.dox.File;
import com.example.dox.ProcessScore;
import com.example.dox.Student;
import com.example.dox.Teacher;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student,String> {
    @Query("select * from teacher t where t.left_select > 0 limit 10 offset 0")
    Flux<Teacher> getAllTeachers();
    @Query("select * from student s where s.teacher_id is null and s.number=:number")
    Mono<Student> getStudentByNumber(@Param("number") String number);
    @Modifying
    @Query("update student s set s.teacher_id=:tid," +
            "s.group_id=:groupId," +
            "s.select_time=:selectTime " +
            "where s.number=:number")
    Mono<Boolean> selectTeacher(@Param("tid") String tid, @Param("number") String number,
                                @Param("selectTime")LocalDateTime selectTime,
                                @Param("groupId") int groupId);
    @Modifying
    @Query("update teacher t set t.left_select=t.left_select-1 where t.id=:tid")
    Mono<Boolean> selectTeacher(@Param("tid") String tid);
    @Query("select count(s.id) from student s ")
    Mono<Integer> numberOfStudents();
    @Query("select distinct count(t.group_id) from teacher t ")
    Mono<Integer> countOfGroup();
    @Query("select * from file f where f.student_number=:number and f.process_id=:pid")
    Mono<File> findByNumberAndPid(@Param("number") String number, @Param("pid") String pid);
    @Modifying
    @Query("update file f set f.detail=:detail where f.student_number=:number and f.process_id=:pid")
    Mono<Boolean> updateByNumberAndPid(@Param("number") String number, @Param("pid") String pid,
                                       @Param("detail") String detail);
    @Query("select s.id from student s where s.number=:number")
    Mono<String> getSidByNumber(@Param("number") String number);
    @Query(("select ps.detail from process_score ps where ps.student_id=:sid and ps.process_id=:pid"))
    Mono<String> getProcessScore(@Param("sid") String sid, @Param("pid") String pid);
    @Query("select p.items from process p where p.id=:pid")
    Mono<String> getByPid(@Param("pid") String pid);
}
