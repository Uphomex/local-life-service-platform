package com.example.life.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.life.entity.ProductAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductAttributeMapper extends BaseMapper<ProductAttribute> {
    
    @Select("SELECT * FROM product_attribute WHERE product_id = #{productId}")
    List<ProductAttribute> findByProductId(@Param("productId") Long productId);
}
