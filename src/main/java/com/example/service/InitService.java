package com.example.service;

import com.example.component.PasswordEncodeComponent;
import com.example.dox.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InitService {
    private final UserRepository userRepository;
    private final PasswordEncodeComponent passwordEncodeComponent;
    @Transactional
    @EventListener(classes = ApplicationReadyEvent.class)
    public Mono<Void> onApplicationReady() {
        String admin = "admin";
        return userRepository.getAdmin()
                .flatMap(r -> {
                    if (r == 0) {
                        User build = User.builder()
                                .number(admin)
                                .name(admin)
                                .password(passwordEncodeComponent.passwordEncoder().encode(admin))
                                .role(User.ROLE_ADMIN)
                                .build();
                        return userRepository.save(build).then();
                    }
                    return Mono.empty();
                });
    }
}
