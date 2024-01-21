package com.example.service;

import com.example.component.PasswordEncodeComponent;
import com.example.dox.Process;
import com.example.dox.Student;
import com.example.dox.Teacher;
import com.example.dox.User;
import com.example.pojo.StartAndEndTime;
import com.example.repository.ProcessRepository;
import com.example.repository.StudentRepository;
import com.example.repository.TeacherRepository;
import com.example.repository.UserRepository;
import com.example.vo.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final StartAndEndTime startAndEndTime;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncodeComponent password;
    private final TeacherRepository teacherRepository;
    public Mono<StartAndEndTime> setTime(LocalDateTime start, LocalDateTime end){
        return Mono.just(this.startAndEndTime)
                .map(time -> {
                    time.setStart(start);
                    time.setEnd(end);
                    return time;
                });
    }
    @Transactional
    public Mono<Void> updateAllPassword() {
        return userRepository.updateAllPassword();
    }
    @Transactional
    public Mono<Void> updatePasswordByUser(String number) {
        return userRepository.updatePasswordByUser(number,password.passwordEncoder().encode(number));
    }
    @Transactional
    public Mono<Void> updatePassword(String number, String newPassword) {
        return userRepository.updatePassword(number,password.passwordEncoder().encode(newPassword));
    }
    private final ProcessRepository processRepository;
    @Transactional
    /*@CacheEvict(value = "process", allEntries = true)*/
    public Mono<Process> postProcess(Process process) {
        return processRepository.save(process);
    }
    @Transactional
    /*@CacheEvict(value = "process", allEntries = true)*/
    public Mono<Boolean> deleteProcess() {
        return processRepository.deleteProcessBy();
    }
    @Transactional
    public Mono<Void> saveStudents(List<Student> students) {
        ArrayList<User> users = new ArrayList<>();
        for (Student student : students) {
            User user = new User();

            user.setNumber(student.getNumber());
            user.setPassword(password.passwordEncoder().encode(student.getNumber()));
            user.setRole(User.ROLE_STUDENT);

            users.add(user);
        }
        userRepository.saveAll(users).collectList().block();
        return studentRepository.saveAll(students).then();
    }
    @Transactional
    public Mono<Void> saveTeachers(List<Teacher> teachers) {
        ArrayList<User> list = new ArrayList<>();
        for (Teacher teacher : teachers) {
            User user = new User();
            String userNumber = teacher.getNumber();
            user.setNumber(userNumber);
            user.setPassword(password.passwordEncoder().encode(userNumber));
            user.setRole(User.ROLE_TEACHER);

            list.add(user);
        }
        System.out.println(list);
        return userRepository.saveAll(list)
                .collectList()
                .thenReturn(teacherRepository.saveAll(teachers))
                .then();
    }
}