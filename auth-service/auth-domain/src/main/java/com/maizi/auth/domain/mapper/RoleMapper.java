package com.maizi.auth.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maizi.auth.domain.entity.Permission;
import com.maizi.auth.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

        /**
         * 根据用户ID查询角色
         */
        @Select("""
                SELECT r.role_name
                FROM roles r
                JOIN user_roles ur ON ur.role_id = r.id
                WHERE ur.user_id = #{userId}
                """)
        List<String> selectRolesByUserId(Long userId);
}