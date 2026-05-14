
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    Optional<User> findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    Optional<User> findByPhone(@Param("phone") String phone);
}
