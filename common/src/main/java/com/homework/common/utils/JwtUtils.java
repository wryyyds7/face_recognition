package com.homework.common.utils;

import com.homework.common.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JwtUtils {
	// TODO: 之后搞环境变量
    public static final String SECRET = "123456123456123456123456123456123456123456123456";
    public static final long EXPIRATIONTIME = 3600*1000;
    
    // Redis keys prefixes
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKEN_PREFIX = "token:user:";

    public static String generateToken(Map<String,Object> claims, RedisService redisService){
    	// 生成新token前，先使旧token失效
    	String userId = String.valueOf(claims.get("userId"));
    	if (userId != null) {
    		invalidateOldToken(userId, redisService);
    	}
    	
    	String token = Jwts.builder()
    			.setExpiration(new Date(System.currentTimeMillis()+EXPIRATIONTIME))
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET)
    			.compact();
    	
    	// 将用户ID与新token关联
    	if (userId != null && redisService != null) {
    		redisService.set(USER_TOKEN_PREFIX + userId, token, EXPIRATIONTIME, TimeUnit.MILLISECONDS);
    	}
    	
    	return token;
    }

    public static Claims parseToken(String token) throws Exception{
    	// 清理token：去除空格和可能的Bearer前缀
    	if (token != null) {
    		token = token.trim();
    		if (token.startsWith("Bearer ")) {
    			token = token.substring(7);
    		}
    	}
    	return Jwts.parser()
    			.setSigningKey(SECRET)
    			.parseClaimsJws(token)
    			.getBody();
    }
	/**
	 * 检查令牌是否在黑名单中
	 * @param token 要检查的令牌
	 * @param redisService Redis服务实例
	 * @return true 表示已退出登录
	 */
	public static boolean isTokenBlacklisted(String token, RedisService redisService) {
		if (redisService != null) {
			return redisService.hasKey(BLACKLIST_PREFIX + token);
		}
		return false;
	}
	
	/**
	 * 根据用户ID使旧token失效
	 * @param userId 用户ID
	 * @param redisService Redis服务实例
	 */
	public static void invalidateOldToken(String userId, RedisService redisService) {
		if (userId != null && redisService != null) {
			String oldToken = (String) redisService.get(USER_TOKEN_PREFIX + userId);
			if (oldToken != null) {
				try {
					logout(oldToken, redisService);
				} catch (Exception e) {
					// 旧token可能已经过期，忽略异常
				}
			}
		}
	}
	
	public static boolean logout(String token, RedisService redisService) throws Exception {
		Claims claims = parseToken(token);
		Date expiration = claims.getExpiration();
		String userId = String.valueOf(claims.get("userId"));

        if (redisService != null) {
            // 2. 将令牌加入黑名单（有效期 = 原令牌剩余时间）
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            if (remainingTime > 0) {
                redisService.set(BLACKLIST_PREFIX + token, expiration, remainingTime, TimeUnit.MILLISECONDS);
            } else {
                // 令牌已过期，设置短暂过期时间
                redisService.set(BLACKLIST_PREFIX + token, expiration, 60, TimeUnit.SECONDS);
            }
            
            // 3. 如果该token是当前用户的活跃token，则从映射中移除
            if (userId != null) {
                String currentToken = (String) redisService.get(USER_TOKEN_PREFIX + userId);
                if (token.equals(currentToken)) {
                    redisService.delete(USER_TOKEN_PREFIX + userId);
                }
            }
        }
        
        return isTokenBlacklisted(token, redisService);
	}
}
