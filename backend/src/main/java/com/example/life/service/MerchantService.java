
package com.example.life.service;

import com.example.life.dto.request.MerchantRegisterRequest;
import com.example.life.dto.response.MerchantResponse;

import java.util.List;

public interface MerchantService {
    
    void registerMerchant(Long userId, MerchantRegisterRequest request);
    
    List<MerchantResponse> findAllActiveMerchants();
    
    List<MerchantResponse> findByCategory(String category);
    
    List<MerchantResponse> findTopRatedMerchants(Integer limit);
    
    MerchantResponse findById(Long id);
    
    void updateMerchant(Long id, MerchantRegisterRequest request);
    
    void updateStatus(Long id, String status);
}
