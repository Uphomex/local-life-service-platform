
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getOrderById, updateOrderStatus, completeOrder, type OrderResponse } from '../api'
import { ElHeader, ElMain, ElCard, ElButton, ElMessage, ElDivider } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const order = ref<OrderResponse | null>(null)
const loading = ref(true)

onMounted(async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  const id = Number(route.params.id)
  if (isNaN(id)) {
    router.push('/orders')
    return
  }
  await loadOrder(id)
})

async function loadOrder(id: number) {
  loading.value = true
  try {
    order.value = await getOrderById(id)
  } catch (error) {
    ElMessage.error('加载订单失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  if (!order.value) return
  try {
    await updateOrderStatus(order.value.id, 'PAID')
    ElMessage.success('支付成功')
    await loadOrder(order.value.id)
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

async function handleComplete() {
  if (!order.value) return
  try {
    await completeOrder(order.value.id)
    ElMessage.success('订单已完成')
    await loadOrder(order.value.id)
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function goBack() {
  router.push('/orders')
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

function formatPayStatus(status: string): string {
  const statusMap: Record<string, string> = {
    'UNPAID': '未支付',
    'PAID': '已支付',
    'REFUNDED': '已退款'
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
          <ElButton text @click="goBack">
            <span>← 返回</span>
          </ElButton>
          <h2 style="margin: 0;">订单详情</h2>
        </div>
      </div>
    </ElHeader>
    
    <ElMain style="padding: 20px;">
      <div v-if="loading" style="text-align: center; padding: 50px;">
        <div style="display: inline-block; width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #667eea; border-radius: 50%; animation: spin 1s linear infinite;"></div>
      </div>
      
      <div v-else-if="!order" style="text-align: center; padding: 50px;">
        <p style="color: #999;">订单不存在</p>
        <ElButton type="primary" @click="goBack">返回订单列表</ElButton>
      </div>
      
      <div v-else>
        <ElCard style="margin-bottom: 20px;">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <h3 style="margin: 0;">{{ order.merchantName }}</h3>
              <p style="color: #999; font-size: 12px; margin-top: 5px;">订单号: {{ order.orderNo }}</p>
            </div>
            <span :style="{ 
              color: order.status === 'COMPLETED' ? '#67c23a' : order.status === 'DELIVERING' ? '#409eff' : order.status === 'PAID' ? '#e6a23c' : '#909399' 
            }">
              {{ formatStatus(order.status) }}
            </span>
          </div>
        </ElCard>
        
        <ElCard style="margin-bottom: 20px;">
          <h4 style="margin-bottom: 15px;">商品清单</h4>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div v-for="item in order.items" :key="item.id" style="display: flex; gap: 15px;">
              <img :src="item.image" :alt="item.productName" style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px;" />
              <div style="flex: 1;">
                <h5 style="margin-bottom: 5px;">{{ item.productName }}</h5>
                <p v-if="item.skuSpec" style="color: #999; font-size: 12px; margin-bottom: 5px;">{{ item.skuSpec }}</p>
                <div style="display: flex; justify-content: space-between;">
                  <span style="color: #ff6b35;">¥{{ item.price }}</span>
                  <span style="color: #999;">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>
        </ElCard>
        
        <ElCard style="margin-bottom: 20px;">
          <h4 style="margin-bottom: 15px;">收货信息</h4>
          <p style="margin-bottom: 5px;"><strong>{{ order.receiverName }}</strong> {{ order.receiverPhone }}</p>
          <p style="color: #666;">{{ order.receiverAddress }}</p>
        </ElCard>
        
        <ElCard>
          <h4 style="margin-bottom: 15px;">订单金额</h4>
          <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
            <span style="color: #999;">商品总额</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
            <span style="color: #999;">配送费</span>
            <span>¥{{ order.deliveryFee }}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
            <span style="color: #999;">优惠</span>
            <span style="color: #67c23a;">-¥{{ order.discountAmount }}</span>
          </div>
          <ElDivider />
          <div style="display: flex; justify-content: space-between;">
            <span style="font-weight: bold;">实付金额</span>
            <span style="color: #ff6b35; font-size: 20px; font-weight: bold;">¥{{ order.payAmount }}</span>
          </div>
        </ElCard>
        
        <div style="margin-top: 30px; display: flex; justify-content: center; gap: 20px;">
          <ElButton v-if="order.status === 'PENDING'" type="primary" @click="handlePay">
            立即支付
          </ElButton>
          <ElButton v-if="order.status === 'DELIVERING'" type="primary" @click="handleComplete">
            确认收货
          </ElButton>
          <ElButton v-if="order.status === 'COMPLETED'" disabled>
            订单已完成
          </ElButton>
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
