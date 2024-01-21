package com.example.filter;

import com.example.vo.ResultVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ResponseHelper {
    private final ObjectMapper objectMapper;
    @SneakyThrows
    public Mono<Void> response(Integer code,@NonNull ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bytes = objectMapper.writeValueAsString(ResultVo.error(code))
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bytes); // 写入响应页面
        return response.writeWith(Mono.just(wrap));
    }
}
