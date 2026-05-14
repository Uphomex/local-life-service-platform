
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface CartItem {
  id: number
  merchantId: number
  merchantName: string
  productId: number
  productName: string
  price: number
  image: string
  quantity: number
  skuId?: number
  skuSpec?: string
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

  function addItem(item: Omit<CartItem, 'id'>) {
    const existingItem = items.value.find(
      i => i.productId === item.productId && i.skuId === item.skuId
    )
    
    if (existingItem) {
      existingItem.quantity += item.quantity
    } else {
      items.value.push({
        ...item,
        id: Date.now()
      })
    }
  }

  function removeItem(id: number) {
    const index = items.value.findIndex(i => i.id === id)
    if (index > -1) {
      items.value.splice(index, 1)
    }
  }

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

  function clearCart() {
    items.value = []
  }

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

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
