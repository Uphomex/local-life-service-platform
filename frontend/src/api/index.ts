/**
 * 本地生活服务平台 - API封装
 *
 * 此文件封装了所有后端API接口，包括：
 * - axios实例配置
 * - 请求/响应拦截器
 * - 类型定义
 * - API方法封装
 *
 * 拦截器功能：
 * - 请求拦截器：自动添加JWT令牌到请求头
 * - 响应拦截器：统一处理响应格式，401未授权自动跳转登录页
 */

// 导入axios
import axios from 'axios'

/**
 * 创建axios实例
 *
 * - baseURL: API基础路径
 * - timeout: 请求超时时间（10秒）
 */
const api = axios.create({
    baseURL: '/api',
    timeout: 10000
})

/**
 * 请求拦截器
 *
 * 在发送请求前自动添加Authorization头（JWT令牌）
 */
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

/**
 * 响应拦截器
 *
 * - 统一返回response.data
 * - 401未授权：清除登录状态并跳转登录页
 */
api.interceptors.response.use(
    (response) => {
        const result = response.data
        if (result.code !== 200) {
            return Promise.reject(new Error(result.message || '请求失败'))
        }
        return result.data
    },
    (error) => {
        if (error.response?.status === 401) {
            // 清除登录状态
            localStorage.removeItem('token')
            localStorage.removeItem('userId')
            localStorage.removeItem('role')
            // 跳转到登录页
            window.location.href = '/login'
        }
        // 新增：提取后端业务错误信息
        const backendMessage = error.response?.data?.message
        if (backendMessage) {
            return Promise.reject(new Error(backendMessage))
        }
        return Promise.reject(error)
    }
)

/**
 * 登录响应类型
 */
export interface LoginResponse {
    userId: number
    username: string
    role: string
    token: string
}

/**
 * 商家响应类型
 */
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

/**
 * 商品响应类型
 */
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

/**
 * 订单响应类型
 */
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

/**
 * 订单项响应类型
 */
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

// ---------------------------
// 认证相关API
// ---------------------------

/**
 * 用户登录
 *
 * @param username 用户名
 * @param password 密码
 */
export async function login(username: string, password: string): Promise<LoginResponse> {
    /*const response = await api.post('/auth/login', {username, password})
    return response.data*/
    // 拦截器已返回 LoginResponse，无需再取 .data
    return await api.post('/auth/login', {username, password}) as unknown as LoginResponse
}

/**
 * 用户注册
 *
 * @param username 用户名
 * @param password 密码
 * @param phone 手机号
 * @param nickname 昵称（可选）
 * @param role 角色（可选）
 */
export async function register(username: string, password: string, phone: string, nickname?: string, role?: string) {
    await api.post('/auth/register', {username, password, phone, nickname, role})
}

// ---------------------------
// 商家相关API
// ---------------------------

/**
 * 获取商家列表
 */
export async function getMerchants(): Promise<MerchantResponse[]> {
    const response = await api.get('/merchant/list')
    return response.data
}

/**
 * 根据ID获取商家详情
 *
 * @param id 商家ID
 */
export async function getMerchantById(id: number): Promise<MerchantResponse> {
    const response = await api.get(`/merchant/${id}`)
    return response.data
}

/**
 * 根据分类获取商家列表
 *
 * @param category 分类名称
 */
export async function getMerchantsByCategory(category: string): Promise<MerchantResponse[]> {
    const response = await api.get(`/merchant/category/${category}`)
    return response.data
}

/**
 * 获取热门商家列表
 *
 * @param limit 数量限制（默认10）
 */
export async function getTopMerchants(limit: number = 10): Promise<MerchantResponse[]> {
    const response = await api.get(`/merchant/top?limit=${limit}`)
    return response.data
}

// ---------------------------
// 商品相关API
// ---------------------------

/**
 * 获取商家商品列表
 *
 * @param merchantId 商家ID
 */
export async function getProductsByMerchant(merchantId: number): Promise<ProductResponse[]> {
    const response = await api.get(`/product/merchant/${merchantId}`)
    return response.data
}

/**
 * 根据ID获取商品详情
 *
 * @param productId 商品ID
 */
export async function getProductById(productId: number): Promise<ProductResponse> {
    const response = await api.get(`/product/${productId}`)
    return response.data
}

/**
 * 根据分类获取商品列表
 *
 * @param category 分类名称
 */
export async function getProductsByCategory(category: string): Promise<ProductResponse[]> {
    const response = await api.get(`/product/category/${category}`)
    return response.data
}

// ---------------------------
// 订单相关API
// ---------------------------

/**
 * 创建订单
 *
 * @param request 订单创建请求
 */
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

/**
 * 获取用户订单列表
 */
export async function getOrdersByUser(): Promise<OrderResponse[]> {
    const response = await api.get('/order/user')
    return response.data
}

/**
 * 根据ID获取订单详情
 *
 * @param orderId 订单ID
 */
export async function getOrderById(orderId: number): Promise<OrderResponse> {
    const response = await api.get(`/order/${orderId}`)
    return response.data
}

/**
 * 更新订单状态
 *
 * @param orderId 订单ID
 * @param status 新状态
 */
export async function updateOrderStatus(orderId: number, status: string) {
    await api.put(`/order/${orderId}/status`, {}, {params: {status}})
}

/**
 * 完成订单
 *
 * @param orderId 订单ID
 */
export async function completeOrder(orderId: number) {
    await api.put(`/order/${orderId}/complete`)
}