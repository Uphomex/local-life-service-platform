
package com.example.life.service.impl;

import com.example.life.common.ServiceException;
import com.example.life.dto.request.MerchantRegisterRequest;
import com.example.life.dto.response.MerchantResponse;
import com.example.life.entity.Merchant;
import com.example.life.entity.User;
import com.example.life.mapper.MerchantMapper;
import com.example.life.mapper.UserMapper;
import com.example.life.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {
    
    private final MerchantMapper merchantMapper;
    private final UserMapper userMapper;
    
    public MerchantServiceImpl(MerchantMapper merchantMapper, UserMapper userMapper) {
        this.merchantMapper = merchantMapper;
        this.userMapper = userMapper;
    }
    
    @Override
    public void registerMerchant(Long userId, MerchantRegisterRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(404, "用户不存在");
        }
        
        Merchant merchant = Merchant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .phone(request.getPhone())
                .category(request.getCategory())
                .businessLicense(request.getBusinessLicense())
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO)
                .minOrderAmount(request.getMinOrderAmount() != null ? request.getMinOrderAmount() : BigDecimal.ZERO)
                .deliveryTime(request.getDeliveryTime() != null ? request.getDeliveryTime() : 30)
                .status("PENDING")
                .rating(BigDecimal.ZERO)
                .reviewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        merchantMapper.insert(merchant);
        
        user.setRole("MERCHANT");
        userMapper.updateById(user);
        
        log.info("Merchant registered successfully: {}", merchant.getName());
    }
    
    @Override
    public List<MerchantResponse> findAllActiveMerchants() {
        return merchantMapper.selectList(null).stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MerchantResponse> findByCategory(String category) {
        return merchantMapper.findByCategory(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MerchantResponse> findTopRatedMerchants(Integer limit) {
        return merchantMapper.findTopRatedMerchants(limit).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public MerchantResponse findById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new ServiceException(404, "商家不存在");
        }
        return toResponse(merchant);
    }
    
    @Override
    public void updateMerchant(Long id, MerchantRegisterRequest request) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new ServiceException(404, "商家不存在");
        }
        
        merchant.setName(request.getName());
        merchant.setAddress(request.getAddress());
        merchant.setLongitude(request.getLongitude());
        merchant.setLatitude(request.getLatitude());
        merchant.setPhone(request.getPhone());
        merchant.setCategory(request.getCategory());
        merchant.setDeliveryFee(request.getDeliveryFee());
        merchant.setMinOrderAmount(request.getMinOrderAmount());
        merchant.setDeliveryTime(request.getDeliveryTime());
        merchant.setUpdatedAt(LocalDateTime.now());
        
        merchantMapper.updateById(merchant);
        log.info("Merchant updated successfully: {}", id);
    }
    
    @Override
    public void updateStatus(Long id, String status) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new ServiceException(404, "商家不存在");
        }
        
        merchant.setStatus(status);
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.updateById(merchant);
        log.info("Merchant status updated: {} -> {}", id, status);
    }
    
    private MerchantResponse toResponse(Merchant merchant) {
        return MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .logo(merchant.getLogo())
                .address(merchant.getAddress())
                .longitude(merchant.getLongitude())
                .latitude(merchant.getLatitude())
                .phone(merchant.getPhone())
                .category(merchant.getCategory())
                .status(merchant.getStatus())
                .deliveryFee(merchant.getDeliveryFee())
                .minOrderAmount(merchant.getMinOrderAmount())
                .deliveryTime(merchant.getDeliveryTime())
                .rating(merchant.getRating())
                .reviewCount(merchant.getReviewCount())
                .build();
    }
}
