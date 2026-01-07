package com.homework.login.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.domain.entity.LoginInfo;
import com.homework.common.domain.entity.Result;
import com.homework.common.domain.entity.UserContext;
import com.homework.common.utils.JwtUtils;
import com.homework.common.utils.IpUtils;
import com.homework.common.config.ServerConfig;
import com.homework.users.domain.dto.SearchUserDTO;
import com.homework.users.domain.dto.UserDTO;
import com.homework.common.domain.entity.User;
import com.homework.users.service.UserService;
import com.homework.common.feign.IpifyClient;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 登录控制类
 *
 */

@RestController
@RequestMapping("/in")
public class LoginController extends BaseController {
//    lombok用不了，所以用不了@slf4j

    private final UserService userService;    //注意，这里换成封装的用户
    private final IpifyClient ipifyClient;

    public LoginController(UserService userService, IpifyClient ipifyClient) {
        this.userService = userService;
        this.ipifyClient = ipifyClient;
    }
    /*
    * */
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO user) {
        log.info("用户登录：{}", user);
        LoginInfo info = null;
        String ip = null;
        
        // 使用ident.me API获取公网IP
        try {
            // 注意：此处为调用第三方API，无需JWT令牌
            // 在实际项目中，应该为Feign客户端配置拦截器，从UserContext获取token并添加到请求头
            // 或者在应用配置中为特定的Feign客户端禁用认证
            ip = ipifyClient.getPublicIp();
            log.info("ident.me API响应：{}", ip);
            // 去除可能的空白字符
            if (ip != null) {
                ip = ip.trim();
            }
        } catch (Exception e) {
            log.error("获取公网IP失败：{}", e.getMessage());
        }
        
        // 如果公网IP获取失败，使用原有方式获取
        if (ip == null || ip.isEmpty()) {
            // 获取当前HTTP请求对象
            HttpServletRequest request = ServerConfig.getRequest();
            // 获取真实IP地址
            ip = IpUtils.getIpAddr(request);
        }
        
        log.info("用户IP地址：{}", ip);
        info = userService.login(UserDTO.toUser(user), ip);
        if (info != null) {
            System.out.println("已经登录完成w");
            log.info("用户登录成功：{}", info);

            HttpHeaders headers = new HttpHeaders();
            String userId = info.getUserId().toString();
            headers.add("userId", userId);
            System.out.println("已经登录完成");
            return Result.success(info);
        }
        log.error("用户登录失败：{}", user.getUserName());
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            log.info("用户注册：{}", user.getUserName());
            Long result = userService.register(user);
            if (result > 0) {
                log.info("用户注册成功：{}", user.getUserName());
                return Result.success(user.getUserName());
            } else {
                log.error("用户注册失败：{}", user);
                return Result.error("注册失败");
            }
        } catch (RuntimeException e) {
            log.error("用户注册失败：{}", user + " 异常为{}", e);
            return Result.error("用户名已存在");
        }

    }

//    @PostMapping("/admin/login")
//    @ApiOperation("管理员登录")
//    public Result adminLogin(@RequestBody AdminDTO admin) {
//        log.info("管理员登录：{}", admin);
//        LoginInfo info = userService.adminLogin(admin);
//        if (info != null) {
//            log.info("管理员登录成功：{}", info);
//            return Result.success(info);
//        }
//        log.error("管理员登录失败：{}", admin);
//        return Result.error("用户名或密码错误");
//    }
//
//    @GetMapping("/test")
//    @ApiOperation("测试接口")
//    public String test() {
//        return "test";
//    }


    @PostMapping("/logout")
    public Result logout(@RequestBody String token) {
        log.info("用户登出");
        UserContext.removeUser();
        boolean result = userService.logout(token);
        if(result){
            return Result.success("登出成功");
        }
        else{
            return Result.error("登出失败");
        }
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody String token) {

        try {
            // 1. 验证旧Token是否有效且未被拉黑
            if (JwtUtils.isTokenBlacklisted(token)) {
                return Result.error("Token已在黑名单中");
            }

            // 2. 解析原始token获取claims信息
            Claims claims = JwtUtils.parseToken(token);

            // 3. 检查token是否即将过期（可选）
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return Result.error("Token已过期");
            }

            // 4. 提取关键信息重新生成新token
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("userId", claims.get("userId"));
            newClaims.put("username", claims.get("username"));
            newClaims.put("password", claims.get("password"));
            newClaims.put("roles", claims.get("roles"));

            // 5. 生成新的token
            String newToken = JwtUtils.generateToken(newClaims);

            // 6. 构造返回的LoginInfo对象
            LoginInfo info = new LoginInfo();
            info.setToken(newToken);
            info.setExpireTime(System.currentTimeMillis() + JwtUtils.EXPIRATIONTIME);
            info.setUserId(((Number) claims.get("userId")).longValue());
            info.setUserName((String) claims.get("username"));
            info.setRoles((List<String>) claims.get("roles"));

            log.info("Token刷新成功");
            return Result.success(info);
        } catch (Exception e) {
            log.error("Token刷新失败", e);
            return Result.error("Token刷新失败: " + e.getMessage());
        }
    }
}
