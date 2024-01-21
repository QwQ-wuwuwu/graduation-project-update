package com.example.service;

import com.example.dox.File;
import com.example.dox.ProcessScore;
import com.example.dox.Student;
import com.example.dox.Teacher;
import com.example.repository.ProcessScoreRepository;
import com.example.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final ProcessScoreRepository processScoreRepository;
    public Mono<List<Student>> getStudentsByTid(String tid) {
        return teacherRepository.getStudentsByTid(tid).collectList();
    }
    public Mono<String> getTid(String uid) {
        return teacherRepository.getTid(uid);
    }
    public Mono<File> getFileByPid(String pid, String number) {
        return teacherRepository.getFileByPidAndNumber(pid,number);
    }
    public Mono<List<Student>> getUnselectStudents() {
        return teacherRepository.getUnselectStudents().collectList();
    }
    public Mono<List<Student>> getByGroup(int groupId) {
        return teacherRepository.getByGroup(groupId).collectList().cache();
    }
    public Mono<Integer> getGroup(String number) {
        return teacherRepository.getGroup(number);
    }
    @Cacheable(value = "items")
    public Mono<String> getByPid(String pid) {
        return teacherRepository.getByPid(pid).cache();
    }
    public Mono<String> getTNameByNumber(String number) {
        return teacherRepository.getTNameByNumber(number);
    }
    public Mono<Void> postProcessScore(ProcessScore processScore) {
        return processScoreRepository.save(processScore).then();
    }
    public Mono<ProcessScore> getPsBySidAndPid(String sid, String pid) {
        return teacherRepository.getPsBySidAndPid(sid,pid);
    }
    @Cacheable(value = "groups")
    public Mono<List<Integer>> getAllGroup() {
        return teacherRepository.getAllGroup().collectList().cache();
    }
    @Cacheable(value = "teachersCache", key = "'groupOf' + #groupId")
    public Mono<List<Teacher>> getTeachersByGroup(int groupId) {
        return teacherRepository.getTeachersByGroup(groupId).collectList().cache();
    }
}
