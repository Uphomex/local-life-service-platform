
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getOrdersByUser, type OrderResponse } from '../api'
import { ElHeader, ElMain, ElCard, ElButton, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const orders = ref<OrderResponse[]>([])
const loading = ref(true)

onMounted(async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  await loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    orders.value = await getOrdersByUser()
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

function viewOrder(id: number) {
  router.push(`/order/${id}`)
}

function goHome() {
  router.push('/')
}

function formatStatus(status: string): string {
  const statusMap: Record<string, string> = {
    'PENDING': '待支付',
    'PAID': '待接单',
    'DELIVERING': '配送中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}
</script>

<template>
  <div style="min-height: 100vh; background-color: #f5f5f5;">
    <ElHeader style="background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.1); padding: 0 20px;">
      <div style="display: flex; justify-content: space-between; align-items: center; height: 100%;">
        <div style="display: flex; align-items: center; gap: 15px;">
          <ElButton text @click="goHome">
            <span>← 返回</span>
          </ElButton>
          <h2 style="margin: 0;">我的订单</h2>
        </div>
      </div>
    </ElHeader>
    
    <ElMain style="padding: 20px;">
      <div v-if="loading" style="text-align: center; padding: 50px;">
        <div style="display: inline-block; width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #667eea; border-radius: 50%; animation: spin 1s linear infinite;"></div>
      </div>
      
      <div v-else-if="orders.length === 0" style="text-align: center; padding: 50px;">
        <p style="color: #999;">暂无订单</p>
        <ElButton type="primary" @click="goHome">去逛逛</ElButton>
      </div>
      
      <div v-else>
        <div style="display: flex; flex-direction: column; gap: 20px;">
          <ElCard v-for="order in orders" :key="order.id" style="cursor: pointer;" @click="viewOrder(order.id)">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div>
                <div style="display: flex; align-items: center; gap: 15px;">
                  <h3 style="margin: 0;">{{ order.merchantName }}</h3>
                  <span :style="{ 
                    color: order.status === 'COMPLETED' ? '#67c23a' : order.status === 'DELIVERING' ? '#409eff' : '#909399' 
                  }">
                    {{ formatStatus(order.status) }}
                  </span>
                </div>
                <p style="color: #999; font-size: 12px; margin-top: 5px;">订单号: {{ order.orderNo }}</p>
                <p style="color: #999; font-size: 12px;">{{ formatDate(order.createdAt) }}</p>
              </div>
              <div style="text-align: right;">
                <p style="color: #ff6b35; font-size: 18px; font-weight: bold;">¥{{ order.payAmount }}</p>
                <p style="color: #999; font-size: 12px;">共{{ order.items.length }}件商品</p>
              </div>
            </div>
            
            <div style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #f0f0f0;">
              <div style="display: flex; gap: 10px; overflow-x: auto;">
                <img 
                  v-for="item in order.items" 
                  :key="item.id"
                  :src="item.image" 
                  :alt="item.productName"
                  style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px; flex-shrink: 0;"
                />
              </div>
            </div>
          </ElCard>
        </div>
      </div>
    </ElMain>
  </div>
</template>

<style>
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
