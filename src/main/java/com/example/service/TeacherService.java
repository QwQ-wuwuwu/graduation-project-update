package com.example.service;

import com.example.dox.File;
import com.example.dox.ProcessScore;
import com.example.dox.Student;
import com.example.dox.Teacher;
import com.example.repository.FileRepository;
import com.example.repository.ProcessScoreRepository;
import com.example.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Mono<File> getFileByPid(String pid, String number,int PNumber) {
        return teacherRepository.getFileByPidAndNumber(pid,number,PNumber);
    }
    public Mono<List<Student>> getUnselectStudents() {
        return teacherRepository.getUnselectStudents().collectList();
    }
    public Mono<List<Student>> getByGroup(int groupId) {
        return teacherRepository.getByGroup(groupId).collectList().cache();
    }
    public Mono<List<Student>> getStudentsByAuth(String number) {
        return teacherRepository.getStudentsByAuth(number).collectList().cache();
    }
    public Mono<Integer> getGroup(String number) {
        return teacherRepository.getGroup(number);
    }
    @Cacheable(value = "processScores")
    public Mono<List<ProcessScore>> getAllProcessScores() {
        return processScoreRepository.findAll().collectList();
    }
    public Mono<ProcessScore> postProcessScore(ProcessScore processScore) {
        return processScoreRepository.save(processScore);
    }
    public Mono<ProcessScore> getProcessScore(String sid,String pid,String tid) {
        return teacherRepository.getProcessScore(sid,pid,tid);
    }
    public Mono<List<ProcessScore>> getProcessScores(String sid,String pid) {
        return teacherRepository.getProcessScores(sid,pid).collectList();
    }
    public Mono<List<ProcessScore>> getProcessScoresByPidAndTid(String tid,String pid) {
        return teacherRepository.getProcessScoresByPidAndTid(tid,pid).collectList();
    }
    @Cacheable(value = "groups")
    public Mono<List<Integer>> getAllGroup() {
        return teacherRepository.getAllGroup().collectList().cache();
    }
    @Cacheable(value = "teachersCache", key = "'groupOf' + #groupId")
    public Mono<List<Teacher>> getTeachersByGroup(int groupId) {
        return teacherRepository.getTeachersByGroup(groupId).collectList().cache();
    }
    private final FileRepository fileRepository;
    public Mono<List<File>> getAllFile(String pid) {
        return fileRepository.getFilesByPid(pid).collectList().cache();
    }
    public Mono<ProcessScore> getOnlyProcessScore(ProcessScore processScore) {
        String pid = processScore.getProcessId();
        String sid = processScore.getStudentId();
        String tid = processScore.getTeacherId();
        return processScoreRepository.getOnlyProcessScore(pid,sid,tid);
    }
    @Transactional
    public Mono<Integer> updateProcessScore(ProcessScore processScore) {
        String pid = processScore.getProcessId();
        String sid = processScore.getStudentId();
        String tid = processScore.getTeacherId();
        return processScoreRepository.updateProcessScore(pid,sid,tid,processScore.getDetail());
    }
}
