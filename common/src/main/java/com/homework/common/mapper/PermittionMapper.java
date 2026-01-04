package com.homework.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface PermittionMapper {
    /**
     * 查询用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    // List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    List<String> selectRolesByUserId(Long userId);
}
