package com.maizi.auth.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maizi.auth.domain.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询权限
     */
    @Select("""
        SELECT p.perm_name
        FROM permissions p
        JOIN role_permissions rp ON rp.perm_id = p.id
        JOIN user_roles ur ON ur.role_id = rp.role_id
        WHERE ur.user_id = #{userId}
        """)
    List<String> selectPermissionsByUserId(Long userId);

}