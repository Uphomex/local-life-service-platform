
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('role')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export interface LoginResponse {
  userId: number
  username: string
  role: string
  token: string
}

export interface MerchantResponse {
  id: number
  name: string
  logo: string
  address: string
  longitude: number
  latitude: number
  phone: string
  category: string
  status: string
  deliveryFee: number
  minOrderAmount: number
  deliveryTime: number
  rating: number
  reviewCount: number
}

export interface ProductResponse {
  id: number
  merchantId: number
  name: string
  description: string
  image: string
  price: number
  originalPrice: number
  stock: number
  soldCount: number
  category: string
  status: string
}

export interface OrderResponse {
  id: number
  orderNo: string
  userId: number
  merchantId: number
  merchantName: string
  riderId: number
  totalAmount: number
  deliveryFee: number
  discountAmount: number
  payAmount: number
  status: string
  payStatus: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  items: OrderItemResponse[]
  createdAt: string
  payTime: string
  deliveryTime: string
  completeTime: string
}

export interface OrderItemResponse {
  id: number
  productId: number
  productName: string
  skuSpec: string
  price: number
  quantity: number
  totalAmount: number
  image: string
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await api.post('/auth/login', { username, password })
  return response.data
}

export async function register(username: string, password: string, phone: string, nickname?: string, role?: string) {
  await api.post('/auth/register', { username, password, phone, nickname, role })
}

export async function getMerchants(): Promise<MerchantResponse[]> {
  const response = await api.get('/merchant/list')
  return response.data
}

export async function getMerchantById(id: number): Promise<MerchantResponse> {
  const response = await api.get(`/merchant/${id}`)
  return response.data
}

export async function getMerchantsByCategory(category: string): Promise<MerchantResponse[]> {
  const response = await api.get(`/merchant/category/${category}`)
  return response.data
}

export async function getTopMerchants(limit: number = 10): Promise<MerchantResponse[]> {
  const response = await api.get(`/merchant/top?limit=${limit}`)
  return response.data
}

export async function getProductsByMerchant(merchantId: number): Promise<ProductResponse[]> {
  const response = await api.get(`/product/merchant/${merchantId}`)
  return response.data
}

export async function getProductById(productId: number): Promise<ProductResponse> {
  const response = await api.get(`/product/${productId}`)
  return response.data
}

export async function getProductsByCategory(category: string): Promise<ProductResponse[]> {
  const response = await api.get(`/product/category/${category}`)
  return response.data
}

export async function createOrder(request: {
  merchantId: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  receiverLongitude: number
  receiverLatitude: number
  items: { productId: number; skuId?: number; quantity: number; skuSpec?: string }[]
  payMethod?: string
}): Promise<OrderResponse> {
  const response = await api.post('/order/create', request)
  return response.data
}

export async function getOrdersByUser(): Promise<OrderResponse[]> {
  const response = await api.get('/order/user')
  return response.data
}

export async function getOrderById(orderId: number): Promise<OrderResponse> {
  const response = await api.get(`/order/${orderId}`)
  return response.data
}

export async function updateOrderStatus(orderId: number, status: string) {
  await api.put(`/order/${orderId}/status`, {}, { params: { status } })
}

export async function completeOrder(orderId: number) {
  await api.put(`/order/${orderId}/complete`)
}
