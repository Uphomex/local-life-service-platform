
/**
 * 商品服务实现类
 * 
 * 实现商品相关的业务逻辑：
 * 1. createProduct() - 创建商品
 * 2. updateProduct() - 更新商品
 * 3. deleteProduct() - 删除商品
 * 4. updateProductStatus() - 更新商品状态
 * 5. getProductById() - 根据ID获取商品详情
 * 6. getProductsByMerchant() - 获取商家商品列表
 * 7. getProductsByCategory() - 获取分类商品列表
 * 8. updateStock() - 更新库存
 * 9. lockStock() - 锁定库存（分布式锁）
 * 10. unlockStock() - 解锁库存
 * 
 * 使用Redis实现库存缓存和分布式锁，确保并发下单时库存一致性。
 */
package com.example.life.service.impl;

import com.example.life.common.ServiceException;
import com.example.life.dto.request.AttributeRequest;
import com.example.life.dto.request.ProductCreateRequest;
import com.example.life.dto.request.SkuRequest;
import com.example.life.dto.response.AttributeResponse;
import com.example.life.dto.response.ProductResponse;
import com.example.life.dto.response.SkuResponse;
import com.example.life.entity.Product;
import com.example.life.entity.ProductAttribute;
import com.example.life.entity.ProductSku;
import com.example.life.mapper.ProductAttributeMapper;
import com.example.life.mapper.ProductMapper;
import com.example.life.mapper.ProductSkuMapper;
import com.example.life.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 * 
 * @Slf4j 自动生成Logger对象
 * @Service 标识这是一个服务组件
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    
    // 商品数据访问层
    private final ProductMapper productMapper;
    
    // 商品属性数据访问层
    private final ProductAttributeMapper attributeMapper;
    
    // 商品SKU数据访问层
    private final ProductSkuMapper skuMapper;
    
    // Redis模板（用于库存缓存和分布式锁）
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis键前缀常量
    private static final String STOCK_LOCK_KEY = "product:stock:lock:";  // 库存锁前缀
    private static final String STOCK_KEY = "product:stock:";             // 库存缓存前缀
    
    /**
     * 构造函数依赖注入
     */
    public ProductServiceImpl(ProductMapper productMapper, ProductAttributeMapper attributeMapper, 
                             ProductSkuMapper skuMapper, RedisTemplate<String, Object> redisTemplate) {
        this.productMapper = productMapper;
        this.attributeMapper = attributeMapper;
        this.skuMapper = skuMapper;
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 创建商品
     * 
     * 创建商品流程：
     * 1. 创建商品主记录
     * 2. 创建商品属性记录（如果有）
     * 3. 创建商品SKU记录（如果有）
     * 4. 将库存同步到Redis缓存
     * 
     * @param merchantId 商家ID
     * @param request 商品创建请求
     * @return 商品响应
     */
    @Override
    @Transactional
    public ProductResponse createProduct(Long merchantId, ProductCreateRequest request) {
        // 1. 创建商品主记录
        Product product = Product.builder()
                .merchantId(merchantId)
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .stock(request.getStock())
                .soldCount(0)
                .category(request.getCategory())
                .status("ACTIVE")           // 默认上架状态
                .sortOrder(request.getSortOrder())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        productMapper.insert(product);
        
        // 2. 创建商品属性记录
        if (request.getAttributes() != null) {
            for (AttributeRequest attr : request.getAttributes()) {
                ProductAttribute attribute = ProductAttribute.builder()
                        .productId(product.getId())
                        .name(attr.getName())
                        .value(attr.getValue())
                        .sortOrder(attr.getSortOrder())
                        .createdAt(LocalDateTime.now())
                        .build();
                attributeMapper.insert(attribute);
            }
        }
        
        // 3. 创建商品SKU记录
        if (request.getSkus() != null) {
            for (SkuRequest sku : request.getSkus()) {
                ProductSku productSku = ProductSku.builder()
                        .productId(product.getId())
                        .skuCode(sku.getSkuCode())
                        .specValues(sku.getSpecValues())
                        .price(sku.getPrice())
                        .stock(sku.getStock())
                        .image(sku.getImage())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                skuMapper.insert(productSku);
            }
        }
        
        // 4. 将库存同步到Redis缓存
        redisTemplate.opsForValue().set(STOCK_KEY + product.getId(), request.getStock());
        
        log.info("Product created successfully: {}", product.getName());
        return getProductById(product.getId());
    }
    
    /**
     * 更新商品
     * 
     * @param merchantId 商家ID
     * @param productId 商品ID
     * @param request 商品更新请求
     * @return 商品响应
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(Long merchantId, Long productId, ProductCreateRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        // 验证商家权限
        if (!product.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权修改此商品");
        }
        
        // 更新商品信息
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImage(request.getImage());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setSortOrder(request.getSortOrder());
        product.setUpdatedAt(LocalDateTime.now());
        
        productMapper.updateById(product);
        
        // 更新Redis库存缓存
        redisTemplate.opsForValue().set(STOCK_KEY + productId, request.getStock());
        
        log.info("Product updated successfully: {}", productId);
        return getProductById(productId);
    }
    
    /**
     * 删除商品
     * 
     * 删除商品时级联删除：
     * 1. 删除商品主记录
     * 2. 删除商品属性记录
     * 3. 删除商品SKU记录
     * 4. 删除Redis缓存
     * 
     * @param merchantId 商家ID
     * @param productId 商品ID
     */
    @Override
    @Transactional
    public void deleteProduct(Long merchantId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        // 验证商家权限
        if (!product.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权删除此商品");
        }
        
        // 级联删除
        productMapper.deleteById(productId);
        attributeMapper.delete(attributeMapper.lambdaQuery().eq(ProductAttribute::getProductId, productId));
        skuMapper.delete(skuMapper.lambdaQuery().eq(ProductSku::getProductId, productId));
        
        // 删除Redis缓存
        redisTemplate.delete(STOCK_KEY + productId);
        
        log.info("Product deleted successfully: {}", productId);
    }
    
    /**
     * 更新商品状态
     * 
     * @param merchantId 商家ID
     * @param productId 商品ID
     * @param status 新状态（ACTIVE:上架, INACTIVE:下架）
     */
    @Override
    public void updateProductStatus(Long merchantId, Long productId, String status) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        // 验证商家权限
        if (!product.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权修改此商品状态");
        }
        
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        
        log.info("Product status updated: {} -> {}", productId, status);
    }
    
    /**
     * 根据ID获取商品详情
     * 
     * @param productId 商品ID
     * @return 商品响应
     */
    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        // 获取商品属性
        List<ProductAttribute> attributes = attributeMapper.selectList(
                attributeMapper.lambdaQuery().eq(ProductAttribute::getProductId, productId));
        
        // 获取商品SKU
        List<ProductSku> skus = skuMapper.findByProductId(productId);
        
        // 构建响应
        return ProductResponse.builder()
                .id(product.getId())
                .merchantId(product.getMerchantId())
                .name(product.getName())
                .description(product.getDescription())
                .image(product.getImage())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .stock(product.getStock())
                .soldCount(product.getSoldCount())
                .category(product.getCategory())
                .status(product.getStatus())
                .attributes(attributes.stream().map(this::toAttributeResponse).collect(Collectors.toList()))
                .skus(skus.stream().map(this::toSkuResponse).collect(Collectors.toList()))
                .build();
    }
    
    /**
     * 获取商家商品列表
     * 
     * @param merchantId 商家ID
     * @return 商品列表
     */
    @Override
    public List<ProductResponse> getProductsByMerchant(Long merchantId) {
        return productMapper.findByMerchantId(merchantId).stream()
                .map(p -> getProductById(p.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取分类商品列表
     * 
     * @param category 分类名称
     * @return 商品列表（最多返回20个）
     */
    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productMapper.findByCategory(category, 20).stream()
                .map(p -> getProductById(p.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * 更新库存
     * 
     * @param productId 商品ID
     * @param quantity 数量（正数增加，负数减少）
     */
    @Override
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        // 更新数据库库存
        product.setStock(product.getStock() + quantity);
        productMapper.updateById(product);
        
        // 更新Redis缓存
        redisTemplate.opsForValue().increment(STOCK_KEY + productId, quantity);
    }
    
    /**
     * 锁定库存（分布式锁实现）
     * 
     * 使用Redis的setIfAbsent实现分布式锁，确保并发下单时库存一致性。
     * 
     * @param productId 商品ID
     * @param quantity 锁定数量
     * @return 是否锁定成功
     */
    @Override
    public boolean lockStock(Long productId, Integer quantity) {
        // 生成锁键
        String lockKey = STOCK_LOCK_KEY + productId;
        
        // 尝试获取分布式锁（30秒过期）
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 30000);
        
        // 未能获取锁
        if (locked == null || !locked) {
            return false;
        }
        
        try {
            // 从Redis获取当前库存
            Integer currentStock = (Integer) redisTemplate.opsForValue().get(STOCK_KEY + productId);
            
            // 如果Redis中没有缓存，从数据库读取并同步到Redis
            if (currentStock == null) {
                Product product = productMapper.selectById(productId);
                if (product == null) {
                    return false;
                }
                currentStock = product.getStock();
                redisTemplate.opsForValue().set(STOCK_KEY + productId, currentStock);
            }
            
            // 检查库存是否足够
            if (currentStock >= quantity) {
                // 扣减库存（Redis）
                redisTemplate.opsForValue().decrement(STOCK_KEY + productId, quantity);
                return true;
            }
            return false;
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
    
    /**
     * 解锁库存（回滚库存）
     * 
     * @param productId 商品ID
     * @param quantity 解锁数量
     */
    @Override
    public void unlockStock(Long productId, Integer quantity) {
        // 将库存加回Redis
        redisTemplate.opsForValue().increment(STOCK_KEY + productId, quantity);
    }
    
    /**
     * 商品属性实体转响应DTO
     * 
     * @param attribute 商品属性实体
     * @return 属性响应
     */
    private AttributeResponse toAttributeResponse(ProductAttribute attribute) {
        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .value(attribute.getValue())
                .build();
    }
    
    /**
     * 商品SKU实体转响应DTO
     * 
     * @param sku 商品SKU实体
     * @return SKU响应
     */
    private SkuResponse toSkuResponse(ProductSku sku) {
        return SkuResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .specValues(sku.getSpecValues())
                .price(sku.getPrice())
                .stock(sku.getStock())
                .image(sku.getImage())
                .build();
    }
}
