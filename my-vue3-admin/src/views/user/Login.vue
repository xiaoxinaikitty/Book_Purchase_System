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
  background:
    radial-gradient(circle at 15% 20%, rgba(255, 232, 201, 0.85), transparent 24%),
    radial-gradient(circle at 85% 18%, rgba(232, 245, 233, 0.75), transparent 22%),
    linear-gradient(135deg, #f8e0cf, #f3d9c7 48%, #d9e8dd);
  display: grid;
  place-items: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.auth-glow {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  filter: blur(4px);
}

.glow-a {
  width: 420px;
  height: 420px;
  top: -120px;
  left: -80px;
  background: rgba(255, 255, 255, 0.3);
}

.glow-b {
  width: 320px;
  height: 320px;
  bottom: -90px;
  right: -60px;
  background: rgba(255, 216, 193, 0.4);
}

.auth-shell {
  width: min(980px, 100%);
  min-height: 560px;
  border-radius: 30px;
  overflow: hidden;
  background: rgba(255, 251, 246, 0.92);
  border: 1px solid rgba(236, 218, 204, 0.92);
  box-shadow: 0 32px 70px rgba(114, 84, 63, 0.18);
  backdrop-filter: blur(18px);
  display: grid;
  grid-template-columns: 1.02fr 1fr;
  z-index: 1;
}

.auth-aside {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.2), transparent 28%),
    linear-gradient(160deg, #b96d42, #d89361 48%, #8ead86);
  color: #fff8f2;
  padding: 46px 38px;
  display: flex;
  flex-direction: column;
}

.aside-logo {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.2);
  display: grid;
  place-items: center;
  box-shadow: 0 14px 24px rgba(92, 57, 35, 0.18);
}

.auth-aside h1 {
  margin-top: 22px;
  font-size: 34px;
}

.auth-aside p {
  margin-top: 12px;
  opacity: 0.94;
  line-height: 1.8;
}

.auth-aside ul {
  list-style: none;
  margin-top: 30px;
  display: grid;
  gap: 14px;
}

.auth-aside li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  padding: 10px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.12);
}

.auth-main {
  padding: 52px 46px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: linear-gradient(180deg, rgba(255, 253, 249, 0.82), rgba(255, 247, 241, 0.92));
}

.auth-header h2 {
  font-size: 32px;
  color: #4a3429;
}

.auth-header p {
  margin-top: 8px;
  color: #8d786b;
}

.auth-form {
  margin-top: 28px;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.auth-form :deep(.el-input__wrapper) {
  border-radius: 14px;
  min-height: 48px;
  background: #fffdfa;
  box-shadow: 0 0 0 1px #ecdccd inset;
}

.auth-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #d97b47 inset;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(217, 123, 71, 0.16), 0 0 0 1px #d97b47 inset;
}

.auth-btn {
  width: 100%;
  min-height: 50px;
  border-radius: 14px;
  font-weight: 700;
  background: linear-gradient(135deg, #d97b47, #8aa784);
  border: none;
  box-shadow: 0 14px 28px rgba(140, 99, 70, 0.16);
}

.auth-links {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: #8b7769;
}

.auth-links a {
  color: #b4683d;
  text-decoration: none;
  font-weight: 700;
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

