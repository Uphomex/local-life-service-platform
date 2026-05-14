
package com.example.life.service;

import com.example.life.dto.request.ProductCreateRequest;
import com.example.life.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    
    ProductResponse createProduct(Long merchantId, ProductCreateRequest request);
    
    ProductResponse updateProduct(Long merchantId, Long productId, ProductCreateRequest request);
    
    void deleteProduct(Long merchantId, Long productId);
    
    void updateProductStatus(Long merchantId, Long productId, String status);
    
    ProductResponse getProductById(Long productId);
    
    List<ProductResponse> getProductsByMerchant(Long merchantId);
    
    List<ProductResponse> getProductsByCategory(String category);
    
    void updateStock(Long productId, Integer quantity);
    
    boolean lockStock(Long productId, Integer quantity);
    
    void unlockStock(Long productId, Integer quantity);
}
