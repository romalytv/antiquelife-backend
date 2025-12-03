package com.antiquelife.antiquelifebackend.users;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtCore {

    // Секретний ключ. В ідеалі він має бути в application.properties, але для старту хардкодимо
    // Він має бути довгим і складним!
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final int lifetime = 86400000; // Час життя токена (24 години в мілісекундах)

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName()) // Зашиваємо логін юзера
                .setIssuedAt(new Date()) // Коли видали
                .setExpiration(new Date((new Date()).getTime() + lifetime)) // Коли протухне
                .signWith(getKey(), SignatureAlgorithm.HS256) // Підписуємо секретним ключем
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // Метод, щоб дістати ім'я з токена (знадобиться пізніше)
    public String getNameFromJwt(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}