
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMerchantById, getProductsByMerchant, type MerchantResponse, type ProductResponse } from '../api'
import { useCartStore } from '../stores/cart'
import { ElHeader, ElMain, ElCard, ElButton, ElMessage, ElBadge } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const merchant = ref<MerchantResponse | null>(null)
const products = ref<ProductResponse[]>([])
const cartItemsCount = ref(0)

onMounted(async () => {
  const id = Number(route.params.id)
  if (isNaN(id)) {
    router.push('/')
    return
  }
  await loadData(id)
})

async function loadData(id: number) {
  try {
    merchant.value = await getMerchantById(id)
    products.value = await getProductsByMerchant(id)
  } catch (error) {
    ElMessage.error('加载数据失败')
    router.push('/')
  }
}

function addToCart(product: ProductResponse) {
  if (!merchant.value) return
  
  cartStore.addItem({
    merchantId: merchant.value.id,
    merchantName: merchant.value.name,
    productId: product.id,
    productName: product.name,
    price: product.price,
    image: product.image,
    quantity: 1
  })
  
  ElMessage.success('已添加到购物车')
}

function goHome() {
  router.push('/')
}

function goToCart() {
  router.push('/orders')
}

const totalCount = computed(() => cartStore.totalCount)
</script>

<template>
  <div style="min-height: 100vh; background-color: #f5f5f5;">
    <ElHeader style="background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.1); padding: 0 20px;">
      <div style="display: flex; justify-content: space-between; align-items: center; height: 100%;">
        <div style="display: flex; align-items: center; gap: 15px;">
          <ElButton text @click="goHome">
            <span>← 返回</span>
          </ElButton>
          <img v-if="merchant" :src="merchant.logo" :alt="merchant.name" style="width: 40px; height: 40px; object-fit: cover; border-radius: 8px;" />
          <div>
            <h2 style="margin: 0; font-size: 18px;">{{ merchant?.name }}</h2>
            <p style="margin: 0; font-size: 12px; color: #999;">{{ merchant?.address }}</p>
          </div>
        </div>
        <div style="display: flex; align-items: center; gap: 10px;">
          <span style="color: #ff6b35; font-weight: bold;">{{ merchant?.rating }}</span>
          <span style="color: #999;">{{ merchant?.reviewCount }}条评价</span>
          <ElBadge :value="totalCount" :hidden="totalCount === 0">
            <ElButton @click="goToCart">
              <span>购物车</span>
            </ElButton>
          </ElBadge>
        </div>
      </div>
    </ElHeader>
    
    <ElMain style="padding: 20px;">
      <div style="margin-bottom: 20px; background: white; padding: 20px; border-radius: 12px;">
        <div style="display: flex; gap: 20px; flex-wrap: wrap;">
          <div style="display: flex; align-items: center; gap: 5px;">
            <span style="color: #999;">配送费</span>
            <span style="color: #ff6b35; font-weight: bold;">¥{{ merchant?.deliveryFee }}</span>
          </div>
          <div style="display: flex; align-items: center; gap: 5px;">
            <span style="color: #999;">起送价</span>
            <span style="color: #666;">¥{{ merchant?.minOrderAmount }}</span>
          </div>
          <div style="display: flex; align-items: center; gap: 5px;">
            <span style="color: #999;">预计送达</span>
            <span style="color: #666;">{{ merchant?.deliveryTime }}分钟</span>
          </div>
        </div>
      </div>
      
      <div>
        <h3 style="margin-bottom: 15px;">商品列表</h3>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 15px;">
          <ElCard v-for="product in products" :key="product.id" style="position: relative;">
            <div style="text-align: center;">
              <img :src="product.image" :alt="product.name" style="width: 150px; height: 150px; object-fit: cover; margin-bottom: 10px;" />
              <h4 style="margin-bottom: 5px; font-size: 16px;">{{ product.name }}</h4>
              <p style="color: #999; font-size: 12px; margin-bottom: 10px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ product.description }}</p>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                  <span style="color: #ff6b35; font-size: 20px; font-weight: bold;">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" style="color: #999; font-size: 12px; text-decoration: line-through; margin-left: 5px;">¥{{ product.originalPrice }}</span>
                </div>
                <ElButton type="primary" size="small" @click="addToCart(product)">
                  加入购物车
                </ElButton>
              </div>
              <p style="color: #999; font-size: 12px; margin-top: 5px;">库存: {{ product.stock }} | 销量: {{ product.soldCount }}</p>
            </div>
          </ElCard>
        </div>
      </div>
    </ElMain>
  </div>
</template>
