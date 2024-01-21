package com.example.controller;

import com.example.dox.User;
import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final UserService userService;
}
