
package com.example.life.controller;

import com.example.life.common.ResponseResult;
import com.example.life.dto.request.ProductCreateRequest;
import com.example.life.dto.response.ProductResponse;
import com.example.life.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Tag(name = "商品接口", description = "商品管理相关接口")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping("/merchant/{merchantId}")
    @Operation(summary = "创建商品", description = "商家创建商品")
    public ResponseEntity<ResponseResult<ProductResponse>> create(
            @PathVariable Long merchantId,
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponse product = productService.createProduct(merchantId, request);
        return ResponseEntity.ok(ResponseResult.success(product));
    }
    
    @PutMapping("/merchant/{merchantId}/{productId}")
    @Operation(summary = "更新商品", description = "商家更新商品信息")
    public ResponseEntity<ResponseResult<ProductResponse>> update(
            @PathVariable Long merchantId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponse product = productService.updateProduct(merchantId, productId, request);
        return ResponseEntity.ok(ResponseResult.success(product));
    }
    
    @DeleteMapping("/merchant/{merchantId}/{productId}")
    @Operation(summary = "删除商品", description = "商家删除商品")
    public ResponseEntity<ResponseResult<Void>> delete(
            @PathVariable Long merchantId,
            @PathVariable Long productId) {
        productService.deleteProduct(merchantId, productId);
        return ResponseEntity.ok(ResponseResult.success("删除成功"));
    }
    
    @PutMapping("/merchant/{merchantId}/{productId}/status")
    @Operation(summary = "更新商品状态", description = "商家更新商品上下架状态")
    public ResponseEntity<ResponseResult<Void>> updateStatus(
            @PathVariable Long merchantId,
            @PathVariable Long productId,
            @RequestParam String status) {
        productService.updateProductStatus(merchantId, productId, status);
        return ResponseEntity.ok(ResponseResult.success("状态更新成功"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详情")
    public ResponseEntity<ResponseResult<ProductResponse>> getById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ResponseResult.success(product));
    }
    
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商家商品", description = "获取商家所有商品")
    public ResponseEntity<ResponseResult<List<ProductResponse>>> getByMerchant(@PathVariable Long merchantId) {
        List<ProductResponse> products = productService.getProductsByMerchant(merchantId);
        return ResponseEntity.ok(ResponseResult.success(products));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "按分类获取商品", description = "根据分类获取商品")
    public ResponseEntity<ResponseResult<List<ProductResponse>>> getByCategory(@PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ResponseResult.success(products));
    }
}
