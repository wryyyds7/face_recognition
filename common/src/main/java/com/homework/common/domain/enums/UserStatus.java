package com.homework.common.domain.enums;

/**
 *
 * 用户状态枚举类
 *
 * 9.23
 */
public enum UserStatus {
    ENABLE("0", "正常"),
    DISABLE("1", "停用");
    private final String code;
    private final String info;
    /**
     * 根据状态码获取枚举值
     * @param code
     * @return
     */
    public static UserStatus valueOfCode(String code) {
        if (code == null || code.isEmpty()) {
            // 默认返回启用状态
            return ENABLE;
        }
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
    UserStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

}
