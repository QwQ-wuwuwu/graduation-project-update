package com.example.controller.exception;

import com.example.exception.XException;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
@Slf4j
@RequiredArgsConstructor // 作用于类构造方法的自动装配，但是内部只能使用final注入或@NonNull
public class ExceptionHandler implements WebExceptionHandler { // 用于将XException异常Json化返回到请求响应,全局异常处理
    // 比ExceptionController方法先执行
    private final ObjectMapper objectMapper;
    @SneakyThrows // 不必显式的try-catch
    @NonNull // 不应该为空
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        Integer code = Code.ERROR;
        if (ex instanceof XException) {
            code = ((XException) ex).getCode();
        }
        String result = objectMapper.writeValueAsString(ResultVo.error(code));
        byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
        ServerHttpResponse response = exchange.getResponse();
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Flux.just(wrap));

        /*response.getCookies(); // 获取cookie
        response.getHeaders(); // 获取响应头
        response.getStatusCode(); // 获取响应状态码
        response.bufferFactory(); // buffer工厂
        response.writeWith(); // 把xxx写到响应页面
        response.setComplete(); // 响应结束*/
    }
}
