/**
 * 本地生活服务平台 - 路由配置
 * 
 * 此文件定义了前端应用的所有路由配置：
 * - 使用Vue Router 4版本
 * - 采用路由懒加载模式，优化首屏加载速度
 * - 定义了7个主要路由
 */

// 导入Vue Router核心模块
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

/**
 * 路由配置数组
 * 
 * 每个路由对象包含：
 * - path: 路由路径
 * - name: 路由名称（用于编程式导航）
 * - component: 路由组件（使用懒加载）
 */
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')    // 首页（商家列表）
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')    // 登录页
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue') // 注册页
  },
  {
    path: '/merchant/:id',
    name: 'MerchantDetail',
    component: () => import('../views/MerchantDetail.vue') // 商家详情页
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue')   // 订单列表页
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue') // 订单详情页
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')  // 用户个人中心页
  }
]

/**
 * 创建路由实例
 * 
 * - 使用createWebHistory()创建HTML5历史模式路由
 * - 传入routes配置数组
 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导出路由实例
export default router