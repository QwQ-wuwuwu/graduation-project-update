package com.example.repository;

import com.example.dox.File;
import com.example.dox.ProcessScore;
import com.example.dox.Student;
import com.example.dox.Teacher;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeacherRepository extends ReactiveCrudRepository<Teacher,String> {
    @Query("select * from student s where s.teacher_id=:tid")
    Flux<Student> getStudentsByTid(@Param("tid") String tid);
    @Query("select t.id from teacher t where t.number=(" +
            "select u.number from user u where u.id=:uid)")
    Mono<String> getTid(@Param("uid") String uid);
    @Query("select * from file f where f.process_id=:pid and f.student_number=:number")
    Mono<File> getFileByPidAndNumber(@Param("pid") String pid, @Param("number") String number);
    @Query("select * from student s where s.teacher_id is null")
    Flux<Student> getUnselectStudents();
    @Query("select * from student s where s.group_id=:group ")
    Flux<Student> getByGroup(@Param("group") int groupId);
    @Query("select t.group_id from teacher t where t.number=:number")
    Mono<Integer> getGroup(@Param("number") String number);
    @Query("select p.items from process p where p.id=:pid")
    Mono<String> getByPid(@Param("pid") String pid);
    @Query("select t.name from teacher t where t.number=:number")
    Mono<String> getTNameByNumber(@Param("number") String number);
    @Query("select * from process_score ps where ps.student_id=:sid and ps.process_id=:pid")
    Mono<ProcessScore> getPsBySidAndPid(@Param("sid") String sid, @Param("pid") String pid);
    @Query("select group_id from teacher")
    Flux<Integer> getAllGroup();
    @Query("select * from teacher t where t.group_id=:groupId")
    Flux<Teacher> getTeachersByGroup(@Param("groupId") int groupId);
}
