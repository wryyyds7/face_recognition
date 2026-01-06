package com.homework.users.domain.entity;

import com.homework.common.domain.entity.BaseEntity;

/**
 * 用户收藏企业实体类
 *
 * @author ruoyi
 */
public class UserFavoriteEnterprise extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
