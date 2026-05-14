
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    @Select("SELECT * FROM review WHERE merchant_id = #{merchantId} ORDER BY created_at DESC")
    List<Review> findByMerchantId(@Param("merchantId") Long merchantId);
    
    @Select("SELECT * FROM review WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<Review> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT * FROM review WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Review> findByUserId(@Param("userId") Long userId);
}
