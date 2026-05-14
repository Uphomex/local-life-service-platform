
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getMerchants, getTopMerchants, type MerchantResponse } from '../api'
import { ElHeader, ElMain, ElFooter, ElCard, ElButton, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const merchants = ref<MerchantResponse[]>([])
const topMerchants = ref<MerchantResponse[]>([])
const categories = ['快餐', '饮品', '烧烤', '火锅', '甜点', '海鲜']
const selectedCategory = ref('')

onMounted(async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  await loadData()
})

async function loadData() {
  try {
    merchants.value = await getMerchants()
    topMerchants.value = await getTopMerchants(5)
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

function viewMerchant(id: number) {
  router.push(`/merchant/${id}`)
}

function goToOrders() {
  router.push('/orders')
}

function goToProfile() {
  router.push('/profile')
}

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div style="min-height: 100vh; background-color: #f5f5f5;">
    <ElHeader style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; display: flex; justify-content: space-between; align-items: center; padding: 0 20px;">
      <div>
        <h1 style="margin: 0;">本地生活服务平台</h1>
      </div>
      <div style="display: flex; gap: 20px; align-items: center;">
        <ElButton text @click="goToOrders" style="color: white;">
          <span>我的订单</span>
        </ElButton>
        <ElButton text @click="goToProfile" style="color: white;">
          <span>个人中心</span>
        </ElButton>
        <ElButton text @click="logout" style="color: white;">
          <span>退出登录</span>
        </ElButton>
      </div>
    </ElHeader>
    
    <ElMain style="padding: 20px;">
      <div style="margin-bottom: 20px;">
        <h2 style="margin-bottom: 15px;">分类</h2>
        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
          <ElButton 
            v-for="category in categories" 
            :key="category"
            :type="selectedCategory === category ? 'primary' : 'default'"
            @click="selectedCategory = selectedCategory === category ? '' : category"
          >
            {{ category }}
          </ElButton>
        </div>
      </div>
      
      <div style="margin-bottom: 30px;">
        <h2 style="margin-bottom: 15px;">热门商家</h2>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;">
          <ElCard 
            v-for="merchant in topMerchants" 
            :key="merchant.id"
            style="cursor: pointer;"
            @click="viewMerchant(merchant.id)"
          >
            <div style="display: flex; gap: 15px;">
              <img :src="merchant.logo" :alt="merchant.name" style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px;" />
              <div style="flex: 1;">
                <h3 style="margin-bottom: 5px;">{{ merchant.name }}</h3>
                <p style="color: #666; font-size: 14px; margin-bottom: 5px;">{{ merchant.address }}</p>
                <div style="display: flex; align-items: center; gap: 10px;">
                  <span style="color: #ff6b35; font-weight: bold;">{{ merchant.rating }}</span>
                  <span style="color: #999; font-size: 12px;">{{ merchant.reviewCount }}条评价</span>
                  <span style="color: #999; font-size: 12px;">{{ merchant.deliveryTime }}分钟送达</span>
                </div>
                <p style="color: #666; font-size: 12px; margin-top: 5px;">配送费 ¥{{ merchant.deliveryFee }} | 起送 ¥{{ merchant.minOrderAmount }}</p>
              </div>
            </div>
          </ElCard>
        </div>
      </div>
      
      <div>
        <h2 style="margin-bottom: 15px;">全部商家</h2>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;">
          <ElCard 
            v-for="merchant in merchants" 
            :key="merchant.id"
            style="cursor: pointer;"
            @click="viewMerchant(merchant.id)"
          >
            <div style="display: flex; gap: 15px;">
              <img :src="merchant.logo" :alt="merchant.name" style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px;" />
              <div style="flex: 1;">
                <h3 style="margin-bottom: 5px;">{{ merchant.name }}</h3>
                <p style="color: #666; font-size: 14px; margin-bottom: 5px;">{{ merchant.address }}</p>
                <div style="display: flex; align-items: center; gap: 10px;">
                  <span style="color: #ff6b35; font-weight: bold;">{{ merchant.rating }}</span>
                  <span style="color: #999; font-size: 12px;">{{ merchant.reviewCount }}条评价</span>
                  <span style="color: #999; font-size: 12px;">{{ merchant.deliveryTime }}分钟送达</span>
                </div>
                <p style="color: #666; font-size: 12px; margin-top: 5px;">配送费 ¥{{ merchant.deliveryFee }} | 起送 ¥{{ merchant.minOrderAmount }}</p>
              </div>
            </div>
          </ElCard>
        </div>
      </div>
    </ElMain>
    
    <ElFooter style="text-align: center; padding: 20px; background: #333; color: white;">
      <p>本地生活服务平台 © 2024</p>
    </ElFooter>
  </div>
</template>
