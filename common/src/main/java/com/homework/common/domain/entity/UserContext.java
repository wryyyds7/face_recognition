package com.homework.common.domain.entity;


import java.util.List;

/**
 * 9.22 用户上下文
 * 用于寻找当前用户
 * 利用ID查找，懒得写User查找了（common类在设计上不应该这么做。靠，设计时大意了，奶奶滴）
 * @author wry
 */
public class UserContext {
    private static final ThreadLocal<Long> user = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> permissions = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> roles = new ThreadLocal<>();
    private static final ThreadLocal<String> ip = new ThreadLocal<>();
    private static final ThreadLocal<String> location = new ThreadLocal<>();
    private static final ThreadLocal<String> adcode = new ThreadLocal<>();
    private static final ThreadLocal<String> token = new ThreadLocal<>();
    
    // 新增：设置用户的权限列表
    public static void setPermissions(List<String> perms) {
        permissions.set(perms);
    }

    public static List<String> getPermissions() {
        return permissions.get();
    }
    
    public static void setUser(Long userId) {
        user.set(userId);
    }
    
    public static Long getUser() {
        return user.get();
    }

    public static void setRoles(List<String> roles) {
        UserContext.roles.set(roles);
    }

    public static List<String> getRoles() {
        return roles.get();
    }
    
    public static void setIp(String ipAddr) {
        ip.set(ipAddr);
    }
    
    public static String getIp() {
        return ip.get();
    }
    
    public static void setLocation(String userLocation) {
        location.set(userLocation);
    }
    
    public static String getLocation() {
        return location.get();
    }
    
    public static void setAdcode(String code) {
        adcode.set(code);
    }
    
    public static String getAdcode() {
        return adcode.get();
    }
    
    public static void setToken(String authToken) {
        token.set(authToken);
    }
    
    public static String getToken() {
        return token.get();
    }
    
    public static void removeUser() {
        user.remove();
        permissions.remove();
        roles.remove();
        ip.remove();
        location.remove();
        adcode.remove();
        token.remove();
    }
}

