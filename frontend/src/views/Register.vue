
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElForm, ElFormItem, ElInput, ElSelect, ElButton, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  nickname: '',
  role: 'USER'
})

const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.password || !form.value.phone) {
    ElMessage.error('请填写必填项')
    return
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    await userStore.register(form.value.username, form.value.password, form.value.phone, form.value.nickname, form.value.role)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <div style="background: white; padding: 40px; border-radius: 12px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); width: 450px;">
      <h2 style="text-align: center; margin-bottom: 30px; color: #333;">用户注册</h2>
      <ElForm model="form" label-width="80px">
        <ElFormItem label="用户名">
          <ElInput v-model="form.username" placeholder="请输入用户名" style="width: 100%;" />
        </ElFormItem>
        <ElFormItem label="密码">
          <ElInput v-model="form.password" type="password" placeholder="请输入密码" style="width: 100%;" />
        </ElFormItem>
        <ElFormItem label="确认密码">
          <ElInput v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" style="width: 100%;" />
        </ElFormItem>
        <ElFormItem label="手机号">
          <ElInput v-model="form.phone" placeholder="请输入手机号" style="width: 100%;" />
        </ElFormItem>
        <ElFormItem label="昵称">
          <ElInput v-model="form.nickname" placeholder="请输入昵称" style="width: 100%;" />
        </ElFormItem>
        <ElFormItem label="角色">
          <ElSelect v-model="form.role" style="width: 100%;">
            <ElSelectOption label="普通用户" value="USER" />
            <ElSelectOption label="商家" value="MERCHANT" />
            <ElSelectOption label="骑手" value="RIDER" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" style="width: 100%;" :loading="loading" @click="handleRegister">
            注册
          </ElButton>
        </ElFormItem>
      </ElForm>
      <p style="text-align: center; margin-top: 20px;">
        已有账号？<a href="/login" style="color: #667eea;">立即登录</a>
      </p>
    </div>
  </div>
</template>
