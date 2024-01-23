package com.example.service;

import com.example.dox.*;
import com.example.dox.Process;
import com.example.exception.XException;
import com.example.repository.FileRepository;
import com.example.repository.ProcessRepository;
import com.example.repository.StudentRepository;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final FileRepository fileRepository;
    public Mono<List<Teacher>> getAllTeacher() {
        return studentRepository.getAllTeachers().collectList();
    }
    public Mono<Student> getStudentByNumber(String number) {
        return studentRepository.getStudentByNumber(number);
    }
    @Transactional
    public Mono<Boolean> selectTeacher(String tid, String number, Integer groupId) {
        LocalDateTime selectTime = LocalDateTime.now();
        return studentRepository.selectTeacher(tid,number,selectTime,groupId)
                .flatMap(flag -> {
                    System.out.println(flag);
                    if (!flag) {
                        return Mono.error(new XException(Code.ERROR, "更新失败"));
                    }
                    return studentRepository.selectTeacher(tid);
                });
    }
    public Mono<Integer> countOfGroup() {
        return studentRepository.countOfGroup();
    }
    @Transactional
    public Mono<Boolean> updateFile(String number, String pid, String detail) {
        return studentRepository.updateByNumberAndPid(number,pid,detail);
    }
    @Transactional
    public Mono<File> findByNumberAndPid(String number, String pid) {
        return studentRepository.findByNumberAndPid(number,pid);
    }
    @Transactional
    public Mono<Void> postFile(File pf) {
        return fileRepository.save(pf).then();
    }
    public Mono<String> getSidByNumber(String number) {
        return studentRepository.getSidByNumber(number);
    }
    public Mono<String> getProcessScore(String sid, String pid) {
        return studentRepository.getProcessScore(sid,pid);
    }
    public Mono<String> getProcessById(String pid) {
        return studentRepository.getByPid(pid);
    }
    public Mono<List<ProcessScore>> getPsBySid(String number) {
        return studentRepository.getSidByNumber(number)
                .flatMap(sid -> studentRepository.getPsBySid(sid).collectList());
    }
}
