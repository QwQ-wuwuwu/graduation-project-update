package com.example.controller;

import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @GetMapping("/test")
    public Mono<ResultVo> test() {
        return Mono.just(ResultVo.success(Code.SUCCESS));
    }
}
