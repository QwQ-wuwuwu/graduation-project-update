package com.example.controller;

import com.example.component.JWTComponent;
import com.example.component.PasswordEncodeComponent;
import com.example.dox.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final PasswordEncodeComponent encode;
    private final JWTComponent jwtComponent;
    @PostMapping("/login")
    public Mono<ResultVo> login(@RequestBody Map<String,String> user, ServerWebExchange exchange) {
        return userService.getUserByNumber(user.get("number"), Integer.parseInt(user.get("role")))
                .filter(u -> encode.passwordEncoder().matches(user.get("password"),u.getPassword()))
                .flatMap(u -> {
                    Map<String,Object> myToken = Map.of("id",u.getId(),
                            "role",u.getRole(),
                            "number",u.getNumber());

                    String token = jwtComponent.encode(myToken);
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().add("token",token);

                    String role = switch (u.getRole()) {
                        case User.ROLE_STUDENT -> "Zvmk";
                        case User.ROLE_TEACHER -> "qWlk";
                        case User.ROLE_ADMIN -> "Vksp";
                        default -> "";
                    }; // 前端标识
                    response.getHeaders().add("role",role);

                    return Mono.just(ResultVo.success(Code.SUCCESS,Map.of("user",u)));
                })
                .defaultIfEmpty(ResultVo.error(Code.FAILLOGGIN,"登陆失败"));
    }
    private final UserRepository userRepository;
    private final PasswordEncodeComponent password;
    @PostMapping("/user")
    public Mono<ResultVo> postUsers(@RequestBody List<User> users) {
        for (User user : users) {
            user.setPassword(password.passwordEncoder().encode(user.getPassword()));
        }
        System.out.println(users);
        return userRepository.saveAll(users).then(Mono.just(ResultVo.success(Code.SUCCESS)));
    }
}