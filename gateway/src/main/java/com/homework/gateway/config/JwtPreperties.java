package com.homework.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * 和util重合了
 */
public class JwtPreperties {
    private static final long EXPIRATIONTIME = 1000*60*60*24;

    //TODO: 之后可以改成jwt的密钥文件形式
    private static final String SECRET = "Iloveyou";

    public static String generateToken(Map<String,Object> claims){
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATIONTIME))
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Claims parseToken(String token) throws Exception{
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
