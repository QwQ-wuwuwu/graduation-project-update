package com.example.repository;

import com.example.dox.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User,String> {
    @Query("select count(u.role) from user u where u.role = 2")
    Mono<Integer> getAdmin();
    @Query("select * from user u where u.number=:number and u.role=:role")
    Mono<User> getUserByNumber(@Param("number") String number, @Param("role") Integer role);
    @Modifying
    @Query("update user u set u.password=u.number")
    Mono<Void> updateAllPassword();
    @Modifying
    @Query("update user u set u.password=:newPw where u.number=:number")
    Mono<Void> updatePassword(@Param("number") String number, @Param("newPw") String pw);
    @Modifying
    @Query("update user u set u.password=:newPw where u.number=:number")
    Mono<Void> updatePasswordByUser(@Param("number") String number, @Param("newPw") String pw);
}
