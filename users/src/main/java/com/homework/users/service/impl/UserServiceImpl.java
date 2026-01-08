package com.homework.users.service.impl;

import com.homework.common.domain.enums.UserStatus;
import com.homework.users.mapper.UserMapper;
import com.homework.users.mapper.UserComplaintMapper;
import com.homework.users.mapper.UserComplaintAttachmentMapper;
import com.homework.users.service.UserService;
import com.homework.users.service.UserCacheService;
import com.homework.common.utils.JwtUtils;
import com.homework.common.service.RedisService;
import com.homework.users.domain.dto.SearchUserDTO;
import com.homework.users.domain.dto.UserDTO;
import com.homework.users.domain.dto.ComplaintDTO;
import com.homework.common.domain.entity.LoginInfo;
import com.homework.common.domain.entity.User;
import com.homework.common.domain.entity.UserContext;
import com.homework.common.feign.BaiduMapClient;
import com.homework.users.domain.entity.UserJobPreference;
import com.homework.users.domain.entity.UserFavoriteEnterprise;
import com.homework.users.domain.entity.UserFavoriteActivity;
import com.homework.users.domain.entity.UserEventRegistration;
import com.homework.users.domain.entity.UserComplaint;
import com.homework.users.domain.entity.UserComplaintAttachment;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserComplaintMapper userComplaintMapper;
    
    @Autowired
    private UserComplaintAttachmentMapper userComplaintAttachmentMapper;

    @Autowired
    private BaiduMapClient baiduMapClient;
    
    @Autowired
    private UserCacheService userCacheService;
    
    @Autowired
    private RedisService redisService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public LoginInfo login(User user, String ip){
        // 1. TODO: 换成用ID或者账号寻找
        User dbUser = userMapper.selectByUsername(user.getUserName());
        String password = dbUser.getPassword();
        if (dbUser == null || !passwordEncoder.matches(user.getPassword(), password)) {
            return null;
        }
        
        // 调用百度地图API获取地理位置信息
            String loginLocation = ip;
            String loginAdcode = null;
            try {
                Map<String, Object> locationResult = baiduMapClient.getLocationByIp(ip);
                log.info("百度地图API返回结果：{}", locationResult);
                
                // 解析返回结果
                if (locationResult != null && locationResult.get("status") != null && (int) locationResult.get("status") == 0) {
                    // 首先检查是否有content字段（最新API结构）
                    if (locationResult.get("content") != null) {
                        Map<String, Object> content = (Map<String, Object>) locationResult.get("content");
                        if (content.get("address") != null) {
                            loginLocation = (String) content.get("address");
                        } else if (content.get("address_detail") != null) {
                            Map<String, Object> addressDetail = (Map<String, Object>) content.get("address_detail");
                            StringBuilder locationBuilder = new StringBuilder();
                            if (addressDetail.get("province") != null) {
                                locationBuilder.append(addressDetail.get("province"));
                            }
                            if (addressDetail.get("city") != null) {
                                locationBuilder.append(addressDetail.get("city"));
                            }
                            if (addressDetail.get("district") != null) {
                                locationBuilder.append(addressDetail.get("district"));
                            }
                            if (locationBuilder.length() > 0) {
                                loginLocation = locationBuilder.toString();
                            }
                        }
                    } else if (locationResult.get("result") != null) {
                        // 兼容旧版API结构（result字段）
                        Map<String, Object> result = (Map<String, Object>) locationResult.get("result");
                        if (result.get("address") != null) {
                            loginLocation = (String) result.get("address");
                        } else {
                            // 拼接省份、城市、区县
                            StringBuilder locationBuilder = new StringBuilder();
                            if (result.get("province") != null) {
                                locationBuilder.append(result.get("province"));
                            }
                            if (result.get("city") != null) {
                                locationBuilder.append(result.get("city"));
                            }
                            if (result.get("district") != null) {
                                locationBuilder.append(result.get("district"));
                            }
                            if (locationBuilder.length() > 0) {
                                loginLocation = locationBuilder.toString();
                            }
                        }
                    }
                    
                    // 解析adcode（区域编号）
                    if (locationResult.get("content") != null) {
                        Map<String, Object> content = (Map<String, Object>) locationResult.get("content");
                        if (content.get("address_detail") != null) {
                            Map<String, Object> addressDetail = (Map<String, Object>) content.get("address_detail");
                            if (addressDetail.get("adcode") != null) {
                                loginAdcode = (String) addressDetail.get("adcode");
                            }
                        }
                    } else if (locationResult.get("result") != null) {
                        // 兼容旧版API结构（result字段）
                        Map<String, Object> result = (Map<String, Object>) locationResult.get("result");
                        if (result.get("address_detail") != null) {
                            Map<String, Object> addressDetail = (Map<String, Object>) result.get("address_detail");
                            if (addressDetail.get("adcode") != null) {
                                loginAdcode = (String) addressDetail.get("adcode");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("调用百度地图API获取地理位置失败：{}", e.getMessage(), e);
                // 失败时使用IP作为位置信息
                loginLocation = ip;
            }
        
        dbUser.setLoginDate(new java.util.Date());
        dbUser.setLoginIp(ip);
        dbUser.setLoginLocation(loginLocation);
        Long a = updateUser(dbUser);
        
        // 设置用户上下文信息
        UserContext.setUser(dbUser.getUserId());
        UserContext.setIp(ip);
        UserContext.setLocation(loginLocation);
        UserContext.setAdcode(loginAdcode);
        
        // 生成Jwt令牌
        List<String> roles = new ArrayList<>();
        // 根据user_type转换为对应的角色
        String userType = dbUser.getUserType();
        if ("00".equals(userType)) {
            roles.add("ADMIN");
        } else if ("01".equals(userType)) {
            roles.add("USER");
        } else if ("02".equals(userType)) {
            roles.add("ENTERPRISE");
        } else {
            // 默认角色
            roles.add("USER");
        }
        
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", dbUser.getUserId());
        claims.put("username", dbUser.getUserName());
        claims.put("password", password);
        claims.put("roles", roles);
        String token = JwtUtils.generateToken(claims, redisService);
        
        // 设置登录时间
        Long loginTime = System.currentTimeMillis();
        // 设置过期时间
        Long expireTime = loginTime + JwtUtils.EXPIRATIONTIME;
        
        return new LoginInfo((long) Math.toIntExact(dbUser.getUserId()), dbUser.getUserName(), dbUser.getNickName(), token, loginTime, expireTime, ip, loginLocation, roles);
    }

    @Override
    public boolean logout(String token) {
        try {
            JwtUtils.logout(token, redisService);
            return true;
        } catch (Exception e) {
            log.error("Logout failed for token: {}", token, e);
            return false;
        }
    }


    @Override
    public User searchUser(SearchUserDTO searchUserDTO) {
        long id = searchUserDTO.getUserId();
        
        // 先检查缓存
        User cachedUser = userCacheService.getUserInfoById(id);
        if (cachedUser != null) {
            log.info("从缓存中获取用户信息，用户ID：{}", id);
            return cachedUser;
        }
        
        // 缓存不存在，查询数据库
        User user = userMapper.searchUserById(id);
        
        // 将结果缓存
        if (user != null) {
            userCacheService.cacheUserInfo(user);
        }
        
        return user;
    }

    @Override
    public Page<User> searchUserByPage(SearchUserDTO searchUserDTO) {
        PageHelper.startPage(searchUserDTO.getPage(), searchUserDTO.getSize());
        Page<User> page = userMapper.searchUserByPage(searchUserDTO);
        return page;
    }

    @Override
    public Long updateUser(User user) {
        user.setUserName(user.getUserName());
        user.setPhonenumber(user.getPhonenumber());
        user.setEmail(user.getEmail());
        
        // 更新数据库
        Long result = userMapper.updateUser(user);
        
        // 删除缓存，确保下次查询时获取最新数据
        if (result > 0) {
            userCacheService.deleteUserCacheById(user.getUserId());
        }
        
        return result;
    }

    @Override
    public Boolean deleteUser(UserDTO UserDTO) {
        Long id = UserDTO.getUserId();
        
        // 删除用户
        Boolean result = userMapper.deleteUser(id);
        
        // 删除缓存
        if (result) {
            userCacheService.deleteUserCacheById(id);
        }
        
        return result;
    }

    @Override
    public User updateUserStatus(UserDTO UserDTO) {
        User user = new User();
        user.setUserId(Long.valueOf(UserDTO.getUserId()));
        // 获取当前用户的状态并转换为枚举
        user = userMapper.searchUserById(user.getUserId());
        UserStatus currentUserStatus = user.getStatus(); // 假设status字段能映射到枚举

        if (currentUserStatus == UserStatus.DISABLE) {
            user.setStatus(UserStatus.ENABLE); // 或者 UserStatus.NORMAL.name()
        } else {
            user.setStatus(UserStatus.DISABLE); // 或者 UserStatus.DISABLE.name()
        }
        
        // 更新数据库
        userMapper.updateUser(user);
        
        // 删除缓存
        userCacheService.deleteUserCacheById(user.getUserId());
        
        return user;
    }

    @Override
    public User[] addUser(User[] user) {
        if(userMapper.addUser(user)!=0){
            return user;
        }
        return null;
    }

    @Override
    public Long register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUserName());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setCreateTime(new java.util.Date());
        
        // 设置密码更新日期为当前时间
        user.setPwdUpdateDate(new java.util.Date());
        
        // 如果没有设置userType，默认为普通用户
        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            user.setUserType("01");
        }
        
        // 如果没有设置nickName，默认使用用户名
        if (user.getNickName() == null || user.getNickName().isEmpty()) {
            user.setNickName(user.getUserName());
        }
        
        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ENABLE);
        }
        
        // 设置默认删除标志为存在
        if (user.getDelFlag() == null || user.getDelFlag().isEmpty()) {
            user.setDelFlag("0");
        }
        
        // 设置默认性别为空
        if (user.getSex() == null) {
            user.setSex("");
        }
        
        // 设置默认头像为空
        if (user.getAvatar() == null) {
            user.setAvatar("");
        }
        
        // 设置默认邮箱为空
        if (user.getEmail() == null) {
            user.setEmail("");
        }
        
        // 设置默认手机号为空
        if (user.getPhonenumber() == null) {
            user.setPhonenumber("");
        }

        // 注册用户
        Long result = userMapper.register(user);
        
        return result;
    }

    @Override
    public User updateUserPassword(User user) {
        return null;
    }
    
    @Override
    public User findByUserName(String userName) {
        try {
            // 先检查缓存
            User cachedUser = userCacheService.getUserInfoByUserName(userName);
            if (cachedUser != null) {
                log.info("从缓存中获取用户信息，用户名：{}", userName);
                return cachedUser;
            }
            
            // 缓存不存在，查询数据库
            User user = userMapper.selectByUsername(userName);
            
            // 将结果缓存
            if (user != null) {
                userCacheService.cacheUserInfo(user);
            }
            
            return user;
        } catch (Exception e) {
            log.error("根据用户名查询用户失败：{}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public User findByUserId(Long userId) {
        try {
            // 先检查缓存
            User cachedUser = userCacheService.getUserInfoById(userId);
            if (cachedUser != null) {
                log.info("从缓存中获取用户信息，用户ID：{}", userId);
                return cachedUser;
            }
            
            // 缓存不存在，查询数据库
            User user = userMapper.searchUserById(userId);
            System.out.println("从数据库中获取user为："+ user.toString());
            // 将结果缓存
            if (user != null) {
                userCacheService.cacheUserInfo(user);
            }
            
            return user;
        } catch (Exception e) {
            log.error("根据用户ID查询用户失败：{}", e.getMessage(), e);
            return null;
        }
    }


    // 投诉相关方法实现
    @Override
    public Long createComplaint(ComplaintDTO complaintDTO) {
        // 创建投诉实体
        UserComplaint complaint = new UserComplaint();
        complaint.setUserId(complaintDTO.getUserId());
        complaint.setComplainedType(complaintDTO.getComplainedType());
        complaint.setComplainedId(complaintDTO.getComplainedId());
        complaint.setComplaintTitle(complaintDTO.getComplaintTitle());
        complaint.setComplaintContent(complaintDTO.getComplaintContent());
        complaint.setComplaintStatus(complaintDTO.getComplaintStatus() != null ? complaintDTO.getComplaintStatus() : "PENDING");
        complaint.setCreateTime(new java.util.Date());
        complaint.setUpdateTime(new java.util.Date());
        
        return userComplaintMapper.insertComplaint(complaint);
    }

    @Override
    public List<UserComplaint> getComplaintList(ComplaintDTO complaintDTO) {
        return userComplaintMapper.selectComplaintList(complaintDTO);
    }

    @Override
    public UserComplaint getComplaintDetail(Long complaintId) {
        return userComplaintMapper.selectComplaintById(complaintId);
    }

    @Override
    public Long updateComplaintStatus(Long complaintId, String status) {
        return userComplaintMapper.updateComplaintStatus(complaintId, status);
    }

    @Override
    public Long handleComplaint(Long complaintId, String status, String handleResult, Long handlerId) {
        return userComplaintMapper.handleComplaint(complaintId, status, handleResult, handlerId);
    }

    @Override
    public List<UserComplaint> getComplaintsByUserId(Long userId) {
        return userComplaintMapper.selectComplaintsByUserId(userId);
    }

    @Override
    public Long uploadComplaintAttachment(UserComplaintAttachment attachment) {
        return userComplaintAttachmentMapper.insertComplaintAttachment(attachment);
    }

    @Override
    public List<UserComplaintAttachment> getComplaintAttachments(Long complaintId) {
        return userComplaintAttachmentMapper.selectAttachmentsByComplaintId(complaintId);
    }
}
