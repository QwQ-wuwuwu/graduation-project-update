package com.example.controller.exception;

import com.example.exception.XException;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.UncategorizedR2dbcException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice // 只处理@Controller类下产生的异常
public class ExceptionController { // 自定义异常处理
    // filter内无效，单独处理。
    @ExceptionHandler(XException.class)
    public Mono<ResultVo> handleXException(Exception exception) {
        return Mono.just(ResultVo.error(Code.ERROR, exception.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public Mono<ResultVo> handleException(Exception exception) {
        return Mono.just(ResultVo.error(Code.ERROR, exception.getMessage()));
    }
    @ExceptionHandler(UncategorizedR2dbcException.class)
    public Mono<ResultVo> handelUncategorizedR2dbcException(UncategorizedR2dbcException exception) {
        return Mono.just(ResultVo.error(Code.ERROR, "唯一约束冲突！" + exception.getMessage()));
    }
}
