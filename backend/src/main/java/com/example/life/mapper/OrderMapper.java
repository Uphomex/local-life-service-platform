
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM `order` WHERE merchant_id = #{merchantId} ORDER BY created_at DESC")
    List<Order> findByMerchantId(@Param("merchantId") Long merchantId);
    
    @Select("SELECT * FROM `order` WHERE rider_id = #{riderId} ORDER BY created_at DESC")
    List<Order> findByRiderId(@Param("riderId") Long riderId);
    
    @Select("SELECT * FROM `order` WHERE status = #{status} ORDER BY created_at DESC")
    List<Order> findByStatus(@Param("status") String status);
}
