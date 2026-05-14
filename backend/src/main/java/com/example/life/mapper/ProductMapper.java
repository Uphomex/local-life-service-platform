
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Select("SELECT * FROM product WHERE merchant_id = #{merchantId} AND status = 'ACTIVE' ORDER BY sort_order ASC")
    List<Product> findByMerchantId(@Param("merchantId") Long merchantId);
    
    @Select("SELECT * FROM product WHERE category = #{category} AND status = 'ACTIVE' LIMIT #{limit}")
    List<Product> findByCategory(@Param("category") String category, @Param("limit") Integer limit);
    
    @Select("SELECT * FROM product WHERE id IN (SELECT product_id FROM order_item WHERE order_id = #{orderId})")
    List<Product> findByOrderId(@Param("orderId") Long orderId);
}
