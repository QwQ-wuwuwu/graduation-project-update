package com.example.filter;

import com.example.dox.User;
import com.example.vo.Code;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Order(2)
@Component
@RequiredArgsConstructor
public class TeacherFilter implements WebFilter {
    private final PathPattern includes = new PathPatternParser().parse("/api/teacher/**");
    private final ResponseHelper responseHelper;
    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        PathContainer pathContainer = exchange.getRequest().getPath().pathWithinApplication();
        if (includes.matches(pathContainer)) {
            int role = (int)exchange.getAttributes().get("role");
            if (role != User.ROLE_TEACHER) {
                return responseHelper.response(Code.UNAUTHORIZED,exchange);
            }
            return chain.filter(exchange);
        }
        return chain.filter(exchange);
    }
}
