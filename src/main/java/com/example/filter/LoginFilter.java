package com.example.filter;

import com.example.component.JWTComponent;
import com.example.exception.XException;
import com.example.vo.Code;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

import java.util.List;

@Component
@Order(1) // 请求依次流经LoginFilter，AdminFilter(TeacherFilter)到达controller
@RequiredArgsConstructor
public class LoginFilter implements WebFilter { // WebFlux框架中的filter本身就是异步的
    private final PathPattern includes = new PathPatternParser().parse("/api/**");
    private final List<PathPattern> excludes = List.of(new PathPatternParser().parse("/api/login"));
    private final JWTComponent jwtComponent;
    private final ResponseHelper responseHelper;
    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        PathContainer pathContainer = exchange.getRequest().getPath().pathWithinApplication(); // 把路径放到容器里
        for (PathPattern exclude : excludes) {
            if (exclude.matches(pathContainer)) {
                return chain.filter(exchange);
            }
        }
        if (!includes.matches(pathContainer)) { // 请求路径是否匹配/api/**模式
            return responseHelper.response(Code.REQUEST_BAD,exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("token");
        if (token == null) {
            return responseHelper.response(Code.UNAUTHORIZED,exchange);
        }
        return Mono.just(jwtComponent.decode(token))
                .flatMap(decode -> {
                    exchange.getAttributes().put("id",decode.getClaim("id").asString());
                    exchange.getAttributes().put("role",decode.getClaim("role").asInt());
                    exchange.getAttributes().put("number",decode.getClaim("number").asString());
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    if (e instanceof XException) {
                        return responseHelper.response(((XException)e).getCode(), exchange);
                    }
                    return responseHelper.response(Code.JSONERROR, exchange);
                });
    }
}
