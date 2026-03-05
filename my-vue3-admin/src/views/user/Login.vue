<template>
  <div class="auth-page">
    <div class="auth-glow glow-a"></div>
    <div class="auth-glow glow-b"></div>

    <div class="auth-shell">
      <aside class="auth-aside">
        <div class="aside-logo">
          <el-icon :size="30"><Reading /></el-icon>
        </div>
        <h1>BookFlow</h1>
        <p>智能图书推荐与购书平台</p>
        <ul>
          <li><el-icon><Check /></el-icon>基于行为的个性化推荐</li>
          <li><el-icon><Check /></el-icon>实时热门与新书发现</li>
          <li><el-icon><Check /></el-icon>一站式购书与订单管理</li>
        </ul>
      </aside>

      <section class="auth-main">
        <div class="auth-header">
          <h2>用户登录</h2>
          <p>登录后继续你的阅读旅程</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="auth-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" class="auth-btn" :loading="loading" @click="handleLogin">
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-links">
          <span>还没有账号？<router-link to="/register">注册新账号</router-link></span>
          <router-link to="/admin/login">管理员入口</router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Reading, Check } from '@element-plus/icons-vue'
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
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  let valid = true
  try {
    await loginFormRef.value.validate()
  } catch {
    valid = false
  }
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(loginForm)
    ElMessage.success(res.message || '登录成功')
    router.push(userStore.isAdmin ? '/admin/home' : '/home')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f6fff, #00b578);
  display: grid;
  place-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.auth-glow {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
  pointer-events: none;
}

.glow-a {
  width: 420px;
  height: 420px;
  top: -120px;
  left: -100px;
}

.glow-b {
  width: 320px;
  height: 320px;
  bottom: -100px;
  right: -80px;
}

.auth-shell {
  width: min(960px, 100%);
  min-height: 520px;
  border-radius: 24px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.24);
  display: grid;
  grid-template-columns: 1fr 1fr;
  z-index: 1;
}

.auth-aside {
  background: linear-gradient(165deg, #0b4bcc, #009e68);
  color: #fff;
  padding: 44px 36px;
  display: flex;
  flex-direction: column;
}

.aside-logo {
  width: 62px;
  height: 62px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  display: grid;
  place-items: center;
}

.auth-aside h1 {
  margin-top: 20px;
  font-size: 34px;
}

.auth-aside p {
  margin-top: 12px;
  opacity: 0.95;
}

.auth-aside ul {
  list-style: none;
  margin-top: 30px;
  display: grid;
  gap: 12px;
}

.auth-aside li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.auth-main {
  padding: 48px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-header h2 {
  font-size: 30px;
  color: #0f172a;
}

.auth-header p {
  margin-top: 8px;
  color: #64748b;
}

.auth-form {
  margin-top: 28px;
}

.auth-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  min-height: 46px;
  box-shadow: 0 0 0 1px #e5eaf2;
}

.auth-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #0f6fff;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(15, 111, 255, 0.18), 0 0 0 1px #0f6fff;
}

.auth-btn {
  width: 100%;
  min-height: 48px;
  border-radius: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, #0f6fff, #00b578);
  border: none;
}

.auth-links {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: #64748b;
}

.auth-links a {
  color: #0f6fff;
  text-decoration: none;
  font-weight: 600;
}

@media (max-width: 900px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-aside {
    display: none;
  }
}

@media (max-width: 560px) {
  .auth-main {
    padding: 34px 24px;
  }
}
</style>
