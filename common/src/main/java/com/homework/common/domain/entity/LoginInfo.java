package com.homework.common.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class LoginInfo {
    private Long userId;
    private String userName;
    private List<String> roles;

    public LoginInfo(Long userId, String username, String name, String token, Long loginTime, Long expireTime, String ipaddr, String loginLocation, List<String> roles) {
        this.userId = userId;
        this.userName = username;
        this.name = name;
        this.token = token;
        this.loginTime = loginTime;
        this.expireTime = expireTime;
        this.ipaddr = ipaddr;
        this.loginLocation = loginLocation;
        this.roles = roles;
    }
    public LoginInfo(){

    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    private String name;
    private String token;
    private Long loginTime;
    private Long expireTime;
    private String ipaddr;
    private String loginLocation;

    public LoginInfo(Long userId, String username, String name, String token, List<String> roles) {
        this.userId = userId;
        this.userName = username;
        this.name = name;
        this.token = token;
        this.roles = roles;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
