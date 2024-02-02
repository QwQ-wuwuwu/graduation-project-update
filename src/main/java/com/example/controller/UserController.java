package com.example.controller;

import com.example.service.StudentService;
import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final StudentService studentService;
    @PutMapping("/password/{newPw}")
    public Mono<ResultVo> updatePasswordByNumber(@PathVariable("newPw") String newPw, @RequestAttribute("number") String number) {
        return userService.updatePasswordByNumber(number,newPw)
                .thenReturn(ResultVo.success(Code.SUCCESS));
    }
    @GetMapping("/process/{pid}")
    public Mono<ResultVo> getProcessById(@PathVariable("pid") String pid) {
        return studentService.getProcessById(pid)
                .map(items -> ResultVo.success(Code.SUCCESS, Map.of("items",items)));
    }
    @GetMapping("/process")
    public Mono<ResultVo> listProcesses() {
        return userService.listProcess()
                .map(list -> ResultVo.success(Code.SUCCESS, Map.of("processes",list)));
    }
}
