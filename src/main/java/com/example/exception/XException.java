package com.example.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XException extends RuntimeException{
    private Integer code;
    public XException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
