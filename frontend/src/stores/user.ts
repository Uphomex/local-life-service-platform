
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as apiLogin, register as apiRegister } from '../api'

export const useUserStore = defineStore('user', () => {
  const userId = ref<number | null>(null)
  const username = ref('')
  const role = ref('')
  const token = ref('')

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

  async function login(username: string, password: string) {
    const result = await apiLogin(username, password)
    userId.value = result.userId
    username.value = result.username
    role.value = result.role
    token.value = result.token
    
    localStorage.setItem('token', result.token)
    localStorage.setItem('userId', result.userId.toString())
    localStorage.setItem('role', result.role)
  }

  async function register(username: string, password: string, phone: string, nickname?: string, role?: string) {
    await apiRegister(username, password, phone, nickname, role)
  }

  function logout() {
    userId.value = null
    username.value = ''
    role.value = ''
    token.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('role')
  }

  function isLoggedIn() {
    return token.value !== ''
  }

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
