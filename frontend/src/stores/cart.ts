/**
 * 本地生活服务平台 - 购物车状态管理
 * 
 * 使用Pinia管理购物车状态，包括：
 * - 购物车商品列表
 * - 添加/删除/修改商品数量
 * - 清空购物车
 * - 计算总价和总数量
 * - 按商家分组
 */

// 导入Pinia状态管理
import { defineStore } from 'pinia'

// 导入Vue响应式API和计算属性
import { ref, computed } from 'vue'

/**
 * 购物车商品接口定义
 */
export interface CartItem {
  id: number                // 购物车项唯一ID
  merchantId: number        // 商家ID
  merchantName: string      // 商家名称
  productId: number         // 商品ID
  productName: string       // 商品名称
  price: number             // 单价
  image: string             // 商品图片
  quantity: number          // 数量
  skuId?: number            // SKU ID（可选）
  skuSpec?: string          // SKU规格（可选）
}

/**
 * 创建购物车状态管理store
 */
export const useCartStore = defineStore('cart', () => {
  // 购物车商品列表
  const items = ref<CartItem[]>([])

  /**
   * 添加商品到购物车
   * 
   * 如果商品已存在（相同productId和skuId），则增加数量
   * 否则添加新商品
   * 
   * @param item 商品信息（不含ID）
   */
  function addItem(item: Omit<CartItem, 'id'>) {
    const existingItem = items.value.find(
      i => i.productId === item.productId && i.skuId === item.skuId
    )
    
    if (existingItem) {
      // 商品已存在，增加数量
      existingItem.quantity += item.quantity
    } else {
      // 添加新商品，使用时间戳作为ID
      items.value.push({
        ...item,
        id: Date.now()
      })
    }
  }

  /**
   * 从购物车移除商品
   * 
   * @param id 购物车项ID
   */
  function removeItem(id: number) {
    const index = items.value.findIndex(i => i.id === id)
    if (index > -1) {
      items.value.splice(index, 1)
    }
  }

  /**
   * 更新商品数量
   * 
   * 如果数量<=0，则移除商品
   * 
   * @param id 购物车项ID
   * @param quantity 新数量
   */
  function updateQuantity(id: number, quantity: number) {
    const item = items.value.find(i => i.id === id)
    if (item) {
      if (quantity <= 0) {
        removeItem(id)
      } else {
        item.quantity = quantity
      }
    }
  }

  /**
   * 清空购物车
   */
  function clearCart() {
    items.value = []
  }

  /**
   * 计算购物车总价
   */
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  /**
   * 计算购物车商品总数
   */
  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  /**
   * 按商家分组购物车商品
   * 
   * 返回格式：[{ merchantId, merchantName, items, subtotal }]
   */
  const groupedByMerchant = computed(() => {
    const groups: { merchantId: number; merchantName: string; items: CartItem[]; subtotal: number }[] = []
    const map = new Map<number, typeof groups[0]>()
    
    items.value.forEach(item => {
      if (!map.has(item.merchantId)) {
        map.set(item.merchantId, {
          merchantId: item.merchantId,
          merchantName: item.merchantName,
          items: [],
          subtotal: 0
        })
      }
      const group = map.get(item.merchantId)!
      group.items.push(item)
      group.subtotal += item.price * item.quantity
    })
    
    return Array.from(map.values())
  })

  // 返回状态和方法
  return {
    items,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    totalPrice,
    totalCount,
    groupedByMerchant
  }
})