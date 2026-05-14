
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

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;
    private final ProductAttributeMapper attributeMapper;
    private final ProductSkuMapper skuMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String STOCK_LOCK_KEY = "product:stock:lock:";
    private static final String STOCK_KEY = "product:stock:";
    
    public ProductServiceImpl(ProductMapper productMapper, ProductAttributeMapper attributeMapper, 
                             ProductSkuMapper skuMapper, RedisTemplate<String, Object> redisTemplate) {
        this.productMapper = productMapper;
        this.attributeMapper = attributeMapper;
        this.skuMapper = skuMapper;
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    @Transactional
    public ProductResponse createProduct(Long merchantId, ProductCreateRequest request) {
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
                .status("ACTIVE")
                .sortOrder(request.getSortOrder())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        productMapper.insert(product);
        
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
        
        redisTemplate.opsForValue().set(STOCK_KEY + product.getId(), request.getStock());
        
        log.info("Product created successfully: {}", product.getName());
        return getProductById(product.getId());
    }
    
    @Override
    @Transactional
    public ProductResponse updateProduct(Long merchantId, Long productId, ProductCreateRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        if (!product.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权修改此商品");
        }
        
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
        redisTemplate.opsForValue().set(STOCK_KEY + productId, request.getStock());
        
        log.info("Product updated successfully: {}", productId);
        return getProductById(productId);
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long merchantId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        if (!product.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权删除此商品");
        }
        
        productMapper.deleteById(productId);
        attributeMapper.delete(attributeMapper.lambdaQuery().eq(ProductAttribute::getProductId, productId));
        skuMapper.delete(skuMapper.lambdaQuery().eq(ProductSku::getProductId, productId));
        
        redisTemplate.delete(STOCK_KEY + productId);
        log.info("Product deleted successfully: {}", productId);
    }
    
    @Override
    public void updateProductStatus(Long merchantId, Long productId, String status) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        if (!product.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权修改此商品状态");
        }
        
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        log.info("Product status updated: {} -> {}", productId, status);
    }
    
    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        List<ProductAttribute> attributes = attributeMapper.selectList(
                attributeMapper.lambdaQuery().eq(ProductAttribute::getProductId, productId));
        
        List<ProductSku> skus = skuMapper.findByProductId(productId);
        
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
    
    @Override
    public List<ProductResponse> getProductsByMerchant(Long merchantId) {
        return productMapper.findByMerchantId(merchantId).stream()
                .map(p -> getProductById(p.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productMapper.findByCategory(category, 20).stream()
                .map(p -> getProductById(p.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(404, "商品不存在");
        }
        
        product.setStock(product.getStock() + quantity);
        productMapper.updateById(product);
        redisTemplate.opsForValue().increment(STOCK_KEY + productId, quantity);
    }
    
    @Override
    public boolean lockStock(Long productId, Integer quantity) {
        String lockKey = STOCK_LOCK_KEY + productId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 30000);
        
        if (locked == null || !locked) {
            return false;
        }
        
        try {
            Integer currentStock = (Integer) redisTemplate.opsForValue().get(STOCK_KEY + productId);
            if (currentStock == null) {
                Product product = productMapper.selectById(productId);
                if (product == null) {
                    return false;
                }
                currentStock = product.getStock();
                redisTemplate.opsForValue().set(STOCK_KEY + productId, currentStock);
            }
            
            if (currentStock >= quantity) {
                redisTemplate.opsForValue().decrement(STOCK_KEY + productId, quantity);
                return true;
            }
            return false;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
    
    @Override
    public void unlockStock(Long productId, Integer quantity) {
        redisTemplate.opsForValue().increment(STOCK_KEY + productId, quantity);
    }
    
    private AttributeResponse toAttributeResponse(ProductAttribute attribute) {
        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .value(attribute.getValue())
                .build();
    }
    
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
