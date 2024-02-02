package com.example.service;

import com.example.dox.*;
import com.example.dox.Process;
import com.example.exception.XException;
import com.example.repository.FileRepository;
import com.example.repository.StudentRepository;
import com.example.vo.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public Mono<Student> getStudent(String number) {
        return studentRepository.getStudent(number);
    }
    public Mono<Integer> countOfGroup() {
        return studentRepository.countOfGroup();
    }
    @Transactional
    public Mono<Void> selectTeacher(String tid, String number, String tname) {
        LocalDateTime selectTime = LocalDateTime.now();
        return studentRepository.selectTeacher(tid).filter(r -> r != 0)
                .switchIfEmpty(Mono.error(new XException(Code.SELECT_FULL,"该导师已被选满")))
                .flatMap(r -> studentRepository.getStudent(number).filter(s -> s.getTeacherId() == null)
                        .switchIfEmpty(Mono.error(new XException(Code.SELECTED,"您已选择导师")))
                        .flatMap(s -> studentRepository.getTeacherG(tid)
                                .flatMap(tg -> studentRepository.countOfGroup()
                                        .flatMap(ag -> {
                                            ArrayList<Integer> integers = new ArrayList<>();
                                            for (int i = 0; i < ag; i++) {
                                                integers.add(i, i + 1);
                                            }
                                            Random random = new Random();
                                            int index = 0;
                                            do {
                                                index = random.nextInt(integers.size());
                                            } while (integers.get(index).equals(tg));
                                            System.out.println(integers.get(index));
                                            return Mono.just(integers.get(index));
                                        }).flatMap(g ->
                                                studentRepository.selectTeacher(tid, number, selectTime, g, tname))
                                )
                        )
                ).then();
    }
    public Mono<List<Process>> getAttachProcess() {
        return studentRepository.getAttachProcess().collectList().cache();
    }
    @Transactional
    public Mono<Boolean> updateFile(String number, String pid, String detail, int numberS) {
        return studentRepository.updateByNumberAndPid(number,pid,detail,numberS);
    }
    @Transactional
    public Mono<File> findByNumberAndPid(String number, String pid,int numberS) {
        return studentRepository.findByNumberAndPid(number,pid,numberS);
    }
    public Mono<List<File>> getFilesByStu(String number) {
        return studentRepository.getFilesByStu(number).collectList();
    }
    @Transactional
    public Mono<Void> postFile(File pf) {
        return fileRepository.save(pf).then();
    }
    public Mono<String> getProcessById(String pid) {
        return studentRepository.getByPid(pid);
    }
}
