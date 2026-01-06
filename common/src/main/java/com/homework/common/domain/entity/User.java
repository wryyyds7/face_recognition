package com.homework.common.domain.entity;

import com.homework.common.domain.enums.UserStatus;
import com.homework.common.domain.entity.Role;

import java.util.Date;
import java.util.List;

public class User extends BaseEntity{
    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    /** 用户类型（00系统用户 01普通用户 02企业用户） */
    private String userType;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phonenumber;

    /** 用户性别 */
    private String sex;

    /** 用户头像 */
    private String avatar;

    /** 密码 */
    private String password;

    /** 账号状态（0正常 1停用） */
    private UserStatus status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 最后登录IP */
    private String loginIp;
    /** 最后登录地址 **/
    private String loginLocation;
    /** 最后登录时间 */
    private Date loginDate;

    /** 密码最后更新时间 */
    private Date pwdUpdateDate;

    /** 角色对象 */
    private List<Role> roles;

    /** 角色组 */
    private Long[] roleIds;

    /** 岗位组 */
    private Long[] postIds;

    /** 角色ID */
    private Long roleId;

    private String avatarPath;

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public User() {
    }

    public User(Long userId, String userName, String password){
        this.userId = userId;
        this.password = password;
        this.userName = userName;
    }

    // 更新构造函数：status 参数类型改为 UserStatus
    public User(Long userId, Long deptId, String userName, String nickName, String userType, String email, String phonenumber, String sex, String avatar, String password, UserStatus status, String delFlag, String loginIp, Date loginDate, Date pwdUpdateDate, List<Role> roles, Long[] roleIds, Long[] postIds, Long roleId) {
        this.userId = userId;
        this.deptId = deptId;
        this.userName = userName;
        this.nickName = nickName;
        this.userType = userType;
        this.email = email;
        this.phonenumber = phonenumber;
        this.sex = sex;
        this.avatar = avatar;
        this.password = password;
        this.status = status;
        this.delFlag = delFlag;
        this.loginIp = loginIp;
        this.loginDate = loginDate;
        this.pwdUpdateDate = pwdUpdateDate;
        this.roles = roles;
        this.roleIds = roleIds;
        this.postIds = postIds;
        this.roleId = roleId;
    }

    public User(Long userId, Long deptId, String userName, String nickName, String userType, String email, String phonenumber, String sex, String avatar, String password, UserStatus status, String delFlag, String loginIp, String loginLocation, Date loginDate, Date pwdUpdateDate, List<Role> roles, Long[] roleIds, Long[] postIds, Long roleId, String avatarPath) {
        this.userId = userId;
        this.deptId = deptId;
        this.userName = userName;
        this.nickName = nickName;
        this.userType = userType;
        this.email = email;
        this.phonenumber = phonenumber;
        this.sex = sex;
        this.avatar = avatar;
        this.password = password;
        this.status = status;
        this.delFlag = delFlag;
        this.loginIp = loginIp;
        this.loginLocation = loginLocation;
        this.loginDate = loginDate;
        this.pwdUpdateDate = pwdUpdateDate;
        this.roles = roles;
        this.roleIds = roleIds;
        this.postIds = postIds;
        this.roleId = roleId;
        this.avatarPath = avatarPath;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    // 更新构造函数：status 参数类型改为 UserStatus
    public User(Long userId, Long deptId, String userName, String nickName, String userType, String email, String phonenumber, String sex, String avatar, String password, UserStatus status, String delFlag, String loginIp, String loginLocation, Date loginDate, Date pwdUpdateDate, List<Role> roles, Long[] roleIds, Long[] postIds, Long roleId) {
        this.userId = userId;
        this.deptId = deptId;
        this.userName = userName;
        this.nickName = nickName;
        this.userType = userType;
        this.email = email;
        this.phonenumber = phonenumber;
        this.sex = sex;
        this.avatar = avatar;
        this.password = password;
        this.status = status;
        this.delFlag = delFlag;
        this.loginIp = loginIp;
        this.loginLocation = loginLocation;
        this.loginDate = loginDate;
        this.pwdUpdateDate = pwdUpdateDate;
        this.roles = roles;
        this.roleIds = roleIds;
        this.postIds = postIds;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
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

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 更新 getter 方法返回类型为 UserStatus
    public UserStatus getStatus() {
        return status;
    }

    // 更新 setter 方法参数类型为 UserStatus
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getPwdUpdateDate() {
        return pwdUpdateDate;
    }

    public void setPwdUpdateDate(Date pwdUpdateDate) {
        this.pwdUpdateDate = pwdUpdateDate;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(Long[] postIds) {
        this.postIds = postIds;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
