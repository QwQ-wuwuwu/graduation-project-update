package com.example.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
public class ResultVo {
    private Integer code;
    private String message;
    private Map<String, Object> data;

    public ResultVo(Integer code, String message, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static ResultVo success(Integer code,Map<String, Object> data) {
        return ResultVo.builder()
                .code(code)
                .data(data).build();
    }
    public static ResultVo success(Integer code) {
        return ResultVo.builder()
                .code(code)
                .build();
    }
    public static ResultVo error(Integer code,String message) {
        return ResultVo.builder()
                .code(code)
                .message(message).build();
    }
    public static ResultVo error(Integer code) {
        return ResultVo.builder()
                .code(code)
                .build();
    }
}
