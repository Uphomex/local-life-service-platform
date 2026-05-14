
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElHeader, ElMain, ElCard, ElButton, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const roleLabel = ref('')

const roleLabels: Record<string, string> = {
  'USER': '普通用户',
  'MERCHANT': '商家',
  'RIDER': '骑手',
  'ADMIN': '管理员'
}

function init() {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  roleLabel.value = roleLabels[userStore.role] || userStore.role
}

init()

function logout() {
  userStore.logout()
  router.push('/login')
  ElMessage.success('退出成功')
}

function goHome() {
  router.push('/')
}

function goToOrders() {
  router.push('/orders')
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
          <h2 style="margin: 0;">个人中心</h2>
        </div>
      </div>
    </ElHeader>
    
    <ElMain style="padding: 20px;">
      <ElCard style="text-align: center; margin-bottom: 20px;">
        <div style="width: 100px; height: 100px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;">
          <span style="color: white; font-size: 40px; font-weight: bold;">{{ userStore.username.charAt(0).toUpperCase() }}</span>
        </div>
        <h3 style="margin-bottom: 5px;">{{ userStore.username }}</h3>
        <p style="color: #667eea; margin-bottom: 20px;">{{ roleLabel }}</p>
        <div style="display: flex; justify-content: center; gap: 30px;">
          <div>
            <p style="font-size: 24px; font-weight: bold; color: #333;">{{ userStore.userId }}</p>
            <p style="font-size: 12px; color: #999;">用户ID</p>
          </div>
        </div>
      </ElCard>
      
      <ElCard style="margin-bottom: 20px;">
        <div style="display: flex; flex-direction: column; gap: 15px;">
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #f0f0f0;">
            <span>我的订单</span>
            <ElButton text @click="goToOrders" style="color: #667eea;">
              <span>查看 →</span>
            </ElButton>
          </div>
        </div>
      </ElCard>
      
      <ElCard>
        <div style="display: flex; flex-direction: column; gap: 15px;">
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0;">
            <span>账号设置</span>
          </div>
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0;">
            <span>帮助中心</span>
          </div>
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0;">
            <span>关于我们</span>
          </div>
        </div>
      </ElCard>
      
      <div style="margin-top: 30px;">
        <ElButton type="danger" style="width: 100%;" @click="logout">
          退出登录
        </ElButton>
      </div>
    </ElMain>
  </div>
</template>
