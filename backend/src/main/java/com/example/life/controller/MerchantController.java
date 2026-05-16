
package com.example.life.controller;

import com.example.life.common.ResponseResult;
import com.example.life.dto.request.MerchantRegisterRequest;
import com.example.life.dto.response.MerchantResponse;
import com.example.life.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
@Tag(name = "商家接口", description = "商家管理相关接口")
public class MerchantController {
    
    private final MerchantService merchantService;
    
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }
    
    @PostMapping("/register")
    @Operation(summary = "商家入驻", description = "商家注册入驻")
    public ResponseEntity<ResponseResult<String>> register(@Valid @RequestBody MerchantRegisterRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        merchantService.registerMerchant(userId, request);
        return ResponseEntity.ok(ResponseResult.success("提交成功，等待审核"));
    }
    
    @GetMapping("/list")
    @Operation(summary = "获取商家列表", description = "获取所有营业中的商家")
    public ResponseEntity<ResponseResult<List<MerchantResponse>>> getMerchantList() {
        List<MerchantResponse> merchants = merchantService.findAllActiveMerchants();
        return ResponseEntity.ok(ResponseResult.success(merchants));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "按分类获取商家", description = "根据分类获取商家列表")
    public ResponseEntity<ResponseResult<List<MerchantResponse>>> getByCategory(@PathVariable String category) {
        List<MerchantResponse> merchants = merchantService.findByCategory(category);
        return ResponseEntity.ok(ResponseResult.success(merchants));
    }
    
    @GetMapping("/top")
    @Operation(summary = "获取热门商家", description = "获取评分最高的商家")
    public ResponseEntity<ResponseResult<List<MerchantResponse>>> getTopRated(@RequestParam(defaultValue = "10") Integer limit) {
        List<MerchantResponse> merchants = merchantService.findTopRatedMerchants(limit);
        return ResponseEntity.ok(ResponseResult.success(merchants));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取商家详情", description = "根据ID获取商家详情")
    public ResponseEntity<ResponseResult<MerchantResponse>> getById(@PathVariable Long id) {
        MerchantResponse merchant = merchantService.findById(id);
        return ResponseEntity.ok(ResponseResult.success(merchant));
    }
}
