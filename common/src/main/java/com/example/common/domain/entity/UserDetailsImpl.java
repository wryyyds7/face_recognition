package com.example.common.domain.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 简化的用户信息类，用于替代Spring Security的UserDetails
 */
public class UserDetailsImpl {
    private final Long id;
    private final String username;
    private final String password;
    private final List<String> roles;

    // 从数据库/JWT 创建用户信息
    public UserDetailsImpl(Long id, String username, String password, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    // 从 JWT 创建用户信息
    public static UserDetailsImpl fromToken(Long userId, String username, String password, List<String> roles) {
        return new UserDetailsImpl(userId, username, password, roles);
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    // 用于获取用户ID（方便后续业务使用）
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    // 将角色转换为Spring Security所需的权限集合
    public List<GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}