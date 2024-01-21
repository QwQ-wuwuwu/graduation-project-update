package com.example;

import com.example.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Random;

@SpringBootTest
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
    @Test
    void test1() {

    }

}
