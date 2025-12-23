<template>
  <div class="admin-login-container">
    <!-- 装饰背景 -->
    <div class="bg-pattern"></div>
    
    <div class="login-card">
      <!-- 头部 Logo -->
      <div class="card-header">
        <div class="logo-wrapper">
          <el-icon :size="36"><Monitor /></el-icon>
        </div>
        <h1>管理后台</h1>
        <p>购书推荐系统管理端</p>
      </div>
      
      <!-- 登录表单 -->
      <el-form 
        ref="loginFormRef" 
        :model="loginForm" 
        :rules="loginRules" 
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="管理员账号"
            size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="登录密码"
            size="large"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            <el-icon v-if="!loading"><Unlock /></el-icon>
            {{ loading ? '登录中...' : '登录系统' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 底部链接 -->
      <div class="card-footer">
        <router-link to="/login" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          返回用户登录
        </router-link>
      </div>
    </div>
    
    <!-- 底部版权 -->
    <div class="copyright">
      © 2024 购书推荐系统 · 管理后台
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Monitor, Unlock, ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const res = await userStore.login(loginForm)
      
      // 验证是否为管理员
      if (!userStore.isAdmin) {
        userStore.clearUserInfo()
        ElMessage.error('该账号没有管理员权限')
        return
      }
      
      ElMessage.success('欢迎回来，' + (userStore.nickname || userStore.username))
      router.push('/admin/home')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #0f172a;
  position: relative;
  overflow: hidden;
}

/* 背景图案 */
.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(59, 130, 246, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(139, 92, 246, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.bg-pattern::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
}

/* 登录卡片 */
.login-card {
  width: 400px;
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 48px 40px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 
    0 0 0 1px rgba(255, 255, 255, 0.05),
    0 20px 50px -15px rgba(0, 0, 0, 0.5);
  position: relative;
  z-index: 1;
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #fff;
  box-shadow: 0 10px 30px -10px rgba(59, 130, 246, 0.5);
}

.card-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.card-header p {
  font-size: 14px;
  color: #94a3b8;
}

/* 登录表单 */
.login-form :deep(.el-input__wrapper) {
  background: rgba(15, 23, 42, 0.6);
  border-radius: 12px;
  padding: 6px 16px;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.5);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3), 0 0 0 1px #3b82f6;
}

.login-form :deep(.el-input__inner) {
  color: #f8fafc;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #64748b;
}

.login-form :deep(.el-input__prefix .el-icon) {
  color: #64748b;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px -10px rgba(59, 130, 246, 0.5);
}

.login-btn:active {
  transform: translateY(0);
}

/* 卡片底部 */
.card-footer {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s;
}

.back-link:hover {
  color: #3b82f6;
}

/* 版权信息 */
.copyright {
  position: absolute;
  bottom: 24px;
  color: #475569;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    padding: 36px 24px;
  }
}
</style>

