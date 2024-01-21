package com.example.filter;

import com.example.dox.User;
import com.example.vo.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
public class AdminFilter implements WebFilter {
    private final PathPattern includes = new PathPatternParser().parse("/api/admin/**");
    private final ResponseHelper responseHelper;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        PathContainer pathContainer = request.getPath().pathWithinApplication();
        if (includes.matches(pathContainer)) {
            int role = (int)exchange.getAttributes().get("role");
            if (role != User.ROLE_ADMIN) {
                return responseHelper.response(Code.UNAUTHORIZED,exchange);
            }
            return chain.filter(exchange);
        }
        return chain.filter(exchange);
    }
}
