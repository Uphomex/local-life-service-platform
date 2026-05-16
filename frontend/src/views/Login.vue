
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})

const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error('登录失败，用户名或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <div style="background: white; padding: 40px; border-radius: 12px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); width: 400px;">
      <h2 style="text-align: center; margin-bottom: 30px; color: #333;">本地生活服务平台</h2>
      <ElForm :model="form" label-width="80px">
        <ElFormItem label="用户名">
          <ElInput 
            v-model="form.username" 
            placeholder="请输入用户名"
            style="width: 100%;"
          />
        </ElFormItem>
        <ElFormItem label="密码">
          <ElInput 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            style="width: 100%;"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton 
            type="primary" 
            style="width: 100%;"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </ElButton>
        </ElFormItem>
      </ElForm>
      <p style="text-align: center; margin-top: 20px;">
        还没有账号？<a href="/register" style="color: #667eea;">立即注册</a>
      </p>
      <p style="text-align: center; margin-top: 10px; font-size: 12px; color: #999;">
        测试账号：admin/admin, user001/admin, merchant001/admin, rider001/admin
      </p>
    </div>
  </div>
</template>
