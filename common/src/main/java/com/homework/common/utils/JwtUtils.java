package com.homework.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JwtUtils {
	// TODO: 之后搞环境变量
    public static final String SECRET = "123456123456123456123456123456123456123456123456";
    public static final long EXPIRATIONTIME = 3600*1000;

	// 黑名单存储TODO之后使用 Redis
	private static final Map<String, Date> BLACKLIST = new ConcurrentHashMap<>();
	// 用户ID与token的映射，用于管理同一用户的多个token
	private static final Map<String, String> USER_TOKEN_MAP = new ConcurrentHashMap<>();
    
    public static String generateToken(Map<String,Object> claims){
    	// 生成新token前，先使旧token失效
    	String userId = String.valueOf(claims.get("userId"));
    	if (userId != null) {
    		invalidateOldToken(userId);
    	}
    	
    	String token = Jwts.builder()
    			.setExpiration(new Date(System.currentTimeMillis()+EXPIRATIONTIME))
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET)
    			.compact();
    	
		// 		// 将用户ID与token关联
    	// String userId = String.valueOf(claims.get("userId"));
    	// 将用户ID与新token关联
    	if (userId != null) {
    		USER_TOKEN_MAP.put(userId, token);
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
	 * @return true 表示已退出登录
	 */
	public static boolean isTokenBlacklisted(String token) {
		if (BLACKLIST.containsKey(token)) {
			// 检查是否已过期
			Date expiration = BLACKLIST.get(token);
			return expiration.before(new Date());
		}
		return false;
	}
	
	/**
	 * 根据用户ID使旧token失效
	 * @param userId 用户ID
	 */
	public static void invalidateOldToken(String userId) {
		if (userId != null && USER_TOKEN_MAP.containsKey(userId)) {
			String oldToken = USER_TOKEN_MAP.get(userId);
			try {
				logout(oldToken);
			} catch (Exception e) {
				// 旧token可能已经过期，忽略异常
			}
		}
	}
	
	public static boolean logout(String token) throws Exception {
		Claims claims = parseToken(token);
		Date expiration = claims.getExpiration();
		String userId = String.valueOf(claims.get("userId"));

		// 2. 将令牌加入黑名单（有效期 = 原令牌剩余时间）
		long remainingTime = expiration.getTime() - System.currentTimeMillis();
		if (remainingTime > 0) {
			BLACKLIST.put(token, new Date(System.currentTimeMillis() + remainingTime));
		} else {
			// 令牌已过期，无需加入黑名单
			BLACKLIST.put(token, new Date());
		}
		
		// 3. 如果该token是当前用户的活跃token，则从映射中移除
		if (userId != null && token.equals(USER_TOKEN_MAP.get(userId))) {
			USER_TOKEN_MAP.remove(userId);
		}
		
		return isTokenBlacklisted(token);
	}
}
