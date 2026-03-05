<template>
  <div class="auth-page reg-theme">
    <div class="auth-glow glow-a"></div>
    <div class="auth-glow glow-b"></div>

    <div class="auth-shell">
      <aside class="auth-aside">
        <div class="aside-logo">
          <el-icon :size="30"><Reading /></el-icon>
        </div>
        <h1>创建你的书架</h1>
        <p>注册后即可获得个性化推荐、收藏管理与订单跟踪能力。</p>
        <ul>
          <li><el-icon><Star /></el-icon>精准推荐喜欢的图书</li>
          <li><el-icon><Collection /></el-icon>收藏并管理你的阅读清单</li>
          <li><el-icon><ShoppingCart /></el-icon>快速下单并查看物流</li>
        </ul>
      </aside>

      <section class="auth-main">
        <div class="auth-header">
          <h2>用户注册</h2>
          <p>填写基础信息，立即开启阅读之旅</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="auth-form"
          @keyup.enter="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="用户名（4-20位）"
              size="large"
              :prefix-icon="User"
              @blur="checkUsernameAvailable"
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="昵称（选填）"
              size="large"
              :prefix-icon="UserFilled"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱（选填）"
              size="large"
              :prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码（6-20位）"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" class="auth-btn" :loading="loading" @click="handleRegister">
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-links">
          <span>已有账号？<router-link to="/login">立即登录</router-link></span>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Lock,
  Message,
  Reading,
  Star,
  Collection,
  ShoppingCart
} from '@element-plus/icons-vue'
import { register, checkUsername } from '@/api/auth'

const router = useRouter()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const validateEmail = (rule, value, callback) => {
  if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
    return
  }
  callback()
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20个字符', trigger: 'blur' }
  ],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const checkUsernameAvailable = async () => {
  if (!registerForm.username || registerForm.username.length < 4) return
  const res = await checkUsername(registerForm.username)
  if (!res.data) {
    ElMessage.warning('该用户名已被注册')
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  let valid = true
  try {
    await registerFormRef.value.validate()
  } catch {
    valid = false
  }
  if (!valid) return

  loading.value = true
  try {
    const res = await register(registerForm)
    ElMessage.success(res.message || '注册成功')
    router.push('/login')
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

.reg-theme {
  background: linear-gradient(135deg, #0168d8, #00a66c);
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
  right: -100px;
}

.glow-b {
  width: 320px;
  height: 320px;
  bottom: -100px;
  left: -80px;
}

.auth-shell {
  width: min(980px, 100%);
  min-height: 560px;
  border-radius: 24px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.24);
  display: grid;
  grid-template-columns: 1fr 1fr;
  z-index: 1;
}

.auth-aside {
  background: linear-gradient(165deg, #0058be, #009a64);
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
  font-size: 30px;
  line-height: 1.2;
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
  padding: 34px 40px;
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
  margin-top: 20px;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 18px;
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
  margin-top: 12px;
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
    padding: 30px 22px;
  }
}
</style>
