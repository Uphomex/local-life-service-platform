/**
 * 本地生活服务平台 - 前端入口文件
 * 
 * 此文件是Vue应用的入口，负责：
 * 1. 创建Vue应用实例
 * 2. 配置Pinia状态管理
 * 3. 配置路由
 * 4. 配置Element Plus UI组件库
 * 5. 挂载应用到DOM
 */

// 导入Vue核心模块
import { createApp } from 'vue'

// 导入Pinia状态管理
import { createPinia } from 'pinia'

// 导入路由配置
import router from './router'

// 导入Element Plus UI组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 导入根组件
import App from './App.vue'

// 创建Vue应用实例
const app = createApp(App)

// 安装Pinia状态管理
app.use(createPinia())

// 安装路由
app.use(router)

// 安装Element Plus组件库
app.use(ElementPlus)

// 将应用挂载到DOM元素（id为app的元素）
app.mount('#app')