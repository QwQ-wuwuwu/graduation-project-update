package com.example.repository;

import com.example.dox.File;
import com.example.dox.Process;
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

@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student,String> {
    @Query("select * from teacher t where t.total > 0 for update ") // 悲观锁
    Flux<Teacher> getAllTeachers();
    @Query("select * from student s where s.teacher_id is null and s.number=:number")
    Mono<Student> getStudentByNumber(@Param("number") String number);
    @Query("select * from student where number=:number")
    Mono<Student> getStudent(@Param("number") String number);
    @Modifying
    @Query("update student s set s.teacher_id=:tid," +
            "s.group_id=:groupId," +
            "s.select_time=:selectTime, " +
            "s.teacher_name=:tname " +
            "where s.number=:number")
    Mono<Integer> selectTeacher(@Param("tid") String tid, @Param("number") String number,
                                @Param("selectTime")LocalDateTime selectTime,
                                @Param("groupId") int groupId, @Param("tname") String tname);
    @Modifying
    @Query("update teacher t set t.total=t.total-1 where t.id=:tid and t.total > 0")
    Mono<Integer> selectTeacher(@Param("tid") String tid);
    @Query("select count(distinct t.group_id) from teacher t ")
    Mono<Integer> countOfGroup();
    @Query("select group_id from teacher where id=:tid")
    Mono<Integer> getTeacherG(@Param("tid") String tid);
    @Query("select * from file f where f.student_number=:number and f.process_id=:pid and f.number=:numberS")
    Mono<File> findByNumberAndPid(@Param("number") String number, @Param("pid") String pid,
                                  @Param("numberS") int numberS);
    @Modifying
    @Query("update file f set f.detail=:detail where f.student_number=:number and f.process_id=:pid and f.number=:numberS")
    Mono<Boolean> updateByNumberAndPid(@Param("number") String number, @Param("pid") String pid,
                                       @Param("detail") String detail, @Param("numberS") int numberS);
    @Query("select * from process p where p.student_attach is not null ")
    Flux<Process> getAttachProcess();
    @Query("select p.items from process p where p.id=:pid")
    Mono<String> getByPid(@Param("pid") String pid);
    @Query("select * from student s where s.group_id=:gid")
    Flux<Student> getStudentsByGroup(@Param("gid") Integer groupId);
    @Query("select * from file f where f.student_number=:number")
    Flux<File> getFilesByStu(@Param("number") String number);
    @Modifying
    @Query("update student s set s.group_id=:group where s.id=:sid")
    Mono<Integer> updateGroup(String sid, int group);
}
