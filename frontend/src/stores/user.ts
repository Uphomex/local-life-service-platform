/**
 * 本地生活服务平台 - 用户状态管理
 * 
 * 使用Pinia管理用户状态，包括：
 * - 用户ID
 * - 用户名
 * - 用户角色
 * - JWT令牌
 * 
 * 提供的方法：
 * - initFromStorage: 从localStorage初始化状态
 * - login: 用户登录
 * - register: 用户注册
 * - logout: 用户登出
 * - isLoggedIn: 判断是否已登录
 */

// 导入Pinia状态管理
import { defineStore } from 'pinia'

// 导入Vue响应式API
import { ref } from 'vue'

// 导入API接口
import { login as apiLogin, register as apiRegister } from '../api'

/**
 * 创建用户状态管理store
 * 
 * 使用Pinia的defineStore创建，采用setup语法
 */
export const useUserStore = defineStore('user', () => {
  // 状态定义
  const userId = ref<number | null>(null)   // 用户ID
  const username = ref('')                  // 用户名
  const role = ref('')                      // 用户角色（USER/MERCHANT/RIDER/ADMIN）
  const token = ref('')                     // JWT令牌

  /**
   * 从localStorage初始化用户状态
   * 
   * 页面刷新时调用，保持登录状态持久化
   */
  function initFromStorage() {
    const storedToken = localStorage.getItem('token')
    const storedUserId = localStorage.getItem('userId')
    const storedRole = localStorage.getItem('role')
    
    if (storedToken && storedUserId && storedRole) {
      token.value = storedToken
      userId.value = parseInt(storedUserId)
      role.value = storedRole
    }
  }

  /**
   * 用户登录
   * 
   * @param username 用户名
   * @param password 密码
   */
  async function login(loginUsername: string, loginPassword: string) {
    const result = await apiLogin(loginUsername, loginPassword)
    userId.value = result.userId
    username.value = result.username
    role.value = result.role
    token.value = result.token
    
    // 将登录信息保存到localStorage
    localStorage.setItem('token', result.token)
    localStorage.setItem('userId', result.userId.toString())
    localStorage.setItem('role', result.role)
  }

  /**
   * 用户注册
   * 
   * @param username 用户名
   * @param password 密码
   * @param phone 手机号
   * @param nickname 昵称（可选）
   * @param role 角色（可选，默认为USER）
   */
  async function register(username: string, password: string, phone: string, nickname?: string, role?: string) {
    await apiRegister(username, password, phone, nickname, role)
  }

  /**
   * 用户登出
   * 
   * 清空状态并删除localStorage中的登录信息
   */
  function logout() {
    userId.value = null
    username.value = ''
    role.value = ''
    token.value = ''
    
    // 删除localStorage中的登录信息
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('role')
  }

  /**
   * 判断用户是否已登录
   * 
   * @return 是否已登录
   */
  function isLoggedIn() {
    return token.value !== ''
  }

  // 返回状态和方法
  return {
    userId,
    username,
    role,
    token,
    initFromStorage,
    login,
    register,
    logout,
    isLoggedIn
  }
})