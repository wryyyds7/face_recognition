package com.homework.common.utils;

import com.homework.common.domain.entity.User;

public class UserUtils {
    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户信息
     */
    public static User getLoginUser() {
        // 移除Spring Security依赖，直接返回null
        // 后续需要根据实际业务需求实现用户信息获取逻辑
        return null;
    }


}
