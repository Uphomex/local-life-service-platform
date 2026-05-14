
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    
    @Select("SELECT * FROM merchant WHERE status = 'ACTIVE' ORDER BY rating DESC, review_count DESC LIMIT #{limit}")
    List<Merchant> findTopRatedMerchants(@Param("limit") Integer limit);
    
    @Select("SELECT * FROM merchant WHERE category = #{category} AND status = 'ACTIVE'")
    List<Merchant> findByCategory(@Param("category") String category);
}
