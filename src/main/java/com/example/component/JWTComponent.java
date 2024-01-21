package com.example.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.exception.XException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JWTComponent {
    @Value("${my.secretKey}")
    private String secretKey;
    //加密
    public String encode(Map<String, Object> map) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000*24);
        return JWT.create()
                .withPayload(map)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(secretKey));
    }
    //解密
    public DecodedJWT decode(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secretKey))
                    .build().verify(token);
        }catch (TokenExpiredException | JWTDecodeException | SignatureVerificationException e) {
            String msg = e instanceof TokenExpiredException ? "过期请重新登录" : "无权限";
            throw new XException(500,msg);
        }
    }
}
