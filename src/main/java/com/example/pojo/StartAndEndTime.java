package com.example.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class StartAndEndTime {
    private LocalDateTime start;
    private LocalDateTime end;
}
