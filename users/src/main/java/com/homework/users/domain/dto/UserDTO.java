package com.homework.users.domain.dto;

import com.homework.common.domain.entity.BaseEntity;
import com.homework.common.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends BaseEntity {
    private Long userId;
    private String userName;
    private String nickName;
    private String password;
    private String phonenumber;
    private String email;
    private String VerificationCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long id) {
        this.userId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String name) {
        this.nickName = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return VerificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        VerificationCode = verificationCode;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUserName(userDTO.getUserName());
        user.setNickName(userDTO.getNickName());
        user.setPassword(userDTO.getPassword());
        user.setPhonenumber(userDTO.getPhonenumber());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setStatus(com.homework.common.domain.enums.UserStatus.ENABLE);
        user.setLoginDate(new Date());
        // TODO: 介入API
        user.setLoginLocation("127.0.0.1");
        user.setLoginIp("127.0.0.1");
        return user;
    }
}
