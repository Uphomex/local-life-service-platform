
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.Rider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RiderMapper extends BaseMapper<Rider> {
    
    @Select("SELECT * FROM rider WHERE status = 'ACTIVE' ORDER BY rating DESC LIMIT #{limit}")
    List<Rider> findActiveRiders(@Param("limit") Integer limit);
    
    @Select("SELECT * FROM rider WHERE user_id = #{userId}")
    Rider findByUserId(@Param("userId") Long userId);
}
