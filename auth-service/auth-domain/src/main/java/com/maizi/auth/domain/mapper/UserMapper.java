package com.maizi.auth.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maizi.auth.domain.entity.Permission;
import com.maizi.auth.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper   extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);
}