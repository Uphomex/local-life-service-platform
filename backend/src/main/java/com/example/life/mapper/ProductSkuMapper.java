
package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {
    
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId}")
    List<ProductSku> findByProductId(@Param("productId") Long productId);
}
