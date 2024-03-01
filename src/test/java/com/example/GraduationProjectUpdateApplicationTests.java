package com.example;

import com.example.component.PasswordEncodeComponent;
import com.example.dox.Student;
import com.example.dox.User;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import com.example.service.StudentService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Random;

@SpringBootTest
@EnableTransactionManagement
class GraduationProjectUpdateApplicationTests {
    @Autowired
    private StudentService studentService;
    @Test
    void contextLoads() {
        studentService.getAllTeacher()
                .subscribe(System.out::println);
        while (true) {
        }
    }
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncodeComponent passwordEncodeComponent;
    @Test
    @Transactional
    void test1() {

    }

}
