package com.example.controller;

import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PutMapping("/password")
    public Mono<ResultVo> updatePasswordByNumber(@RequestParam("newPw") String newPw, @RequestAttribute("number") String number) {
        return userService.updatePasswordByNumber(number,newPw)
                .thenReturn(ResultVo.success(Code.SUCCESS));
    }
}
