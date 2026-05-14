
/**
 * 商家实体类
 * 
 * 映射数据库表 `merchant`，存储平台商家信息。
 * 商家需要经过审核才能上线营业。
 * 
 * 使用Lombok注解简化代码：
 * - @Data：自动生成getter、setter、toString、equals、hashCode方法
 * - @Builder：提供构建器模式
 * - @NoArgsConstructor：生成无参构造函数
 * - @AllArgsConstructor：生成全参构造函数
 */
package com.example.life.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家实体
 * 
 * @TableName("merchant") 指定映射的数据库表名
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant")
public class Merchant {
    
    /**
     * 商家ID（主键）
     * 
     * @TableId(type = IdType.AUTO) 表示主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商家名称
     * 店铺名称，显示在平台上
     */
    private String name;
    
    /**
     * 商家Logo URL
     * 店铺头像图片地址
     */
    private String logo;
    
    /**
     * 商家地址
     * 店铺详细地址
     */
    private String address;
    
    /**
     * 经度
     * 商家地理位置经度坐标，用于附近商家搜索
     */
    private BigDecimal longitude;
    
    /**
     * 纬度
     * 商家地理位置纬度坐标，用于附近商家搜索
     */
    private BigDecimal latitude;
    
    /**
     * 商家联系电话
     * 店铺联系电话
     */
    private String phone;
    
    /**
     * 商家分类
     * 如：快餐、饮品、烧烤、火锅、甜点、海鲜等
     */
    private String category;
    
    /**
     * 营业执照编号
     * 商家入驻时提交的营业执照信息
     */
    private String businessLicense;
    
    /**
     * 商家状态
     * 可选值：PENDING（待审核）、ACTIVE（营业中）、INACTIVE（停业）
     */
    private String status;
    
    /**
     * 配送费
     * 基础配送费用
     */
    private BigDecimal deliveryFee;
    
    /**
     * 起送金额
     * 最低订单金额要求
     */
    private BigDecimal minOrderAmount;
    
    /**
     * 预计配送时间（分钟）
     * 平均送达时间
     */
    private Integer deliveryTime;
    
    /**
     * 商家评分
     * 基于用户评价计算的评分（1-5分）
     */
    private BigDecimal rating;
    
    /**
     * 评价数量
     * 用户评价总数
     */
    private Integer reviewCount;
    
    /**
     * 创建时间
     * 商家入驻时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 商家信息最后更新时间
     */
    private LocalDateTime updatedAt;
}
