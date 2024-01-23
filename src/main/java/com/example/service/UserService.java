package com.example.service;

import com.example.component.PasswordEncodeComponent;
import com.example.dox.Process;
import com.example.dox.User;
import com.example.repository.ProcessRepository;
import com.example.repository.StudentRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncodeComponent password;
    private final UserRepository userRepository;

    public Mono<User> getUserByNumber(String number, Integer role) {
        return userRepository.getUserByNumber(number,role);
    }
    private final ProcessRepository processRepository;
    @Cacheable(value = "process")
    public Mono<List<Process>> listProcess() {
        return processRepository.findAll().collectList().cache();
    }
    @Transactional
    public Mono<Void> updatePasswordByNumber(String number, String newPw) {
        return userRepository.updatePassword(number,password.passwordEncoder().encode(newPw));
    }
}
