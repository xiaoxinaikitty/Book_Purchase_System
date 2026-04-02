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
  background:
    radial-gradient(circle at 18% 20%, rgba(255, 241, 203, 0.82), transparent 24%),
    radial-gradient(circle at 82% 24%, rgba(227, 239, 255, 0.55), transparent 20%),
    linear-gradient(135deg, #f8ddca, #f5d8d1 46%, #dbead9);
  display: grid;
  place-items: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.reg-theme {
  background:
    radial-gradient(circle at 18% 20%, rgba(255, 241, 203, 0.82), transparent 24%),
    radial-gradient(circle at 82% 24%, rgba(227, 239, 255, 0.55), transparent 20%),
    linear-gradient(135deg, #f8ddca, #f1d4cb 46%, #d7e7dc);
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
  right: -100px;
  background: rgba(255, 255, 255, 0.3);
}

.glow-b {
  width: 320px;
  height: 320px;
  bottom: -100px;
  left: -80px;
  background: rgba(255, 220, 206, 0.34);
}

.auth-shell {
  width: min(1020px, 100%);
  min-height: 600px;
  border-radius: 30px;
  overflow: hidden;
  background: rgba(255, 251, 246, 0.94);
  border: 1px solid rgba(236, 218, 204, 0.92);
  box-shadow: 0 32px 70px rgba(114, 84, 63, 0.18);
  backdrop-filter: blur(18px);
  display: grid;
  grid-template-columns: 1.04fr 1fr;
  z-index: 1;
}

.auth-aside {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.2), transparent 28%),
    linear-gradient(165deg, #8fa784, #d48c5a 54%, #e6b481);
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
  font-size: 30px;
  line-height: 1.2;
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
  padding: 38px 42px;
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
  margin-top: 22px;
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
  margin-top: 12px;
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
    padding: 30px 22px;
  }
}
</style>

