<template>
  <div class="profile-page">
    <header class="profile-header">
      <div class="header-left">
        <h1>个人中心</h1>
        <p>管理个人资料与安全设置</p>
      </div>
      <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
    </header>

    <div class="profile-grid">
      <section class="card avatar-card">
        <div class="avatar-wrap">
          <el-avatar :size="96" :src="avatarPreview">
            {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="meta">
            <div class="name">{{ userStore.nickname || userStore.username }}</div>
            <div class="sub">账号：{{ userStore.username }}</div>
            <el-tag size="small" :type="userStore.isAdmin ? 'warning' : 'success'">
              {{ userStore.isAdmin ? '管理员' : '普通用户' }}
            </el-tag>
          </div>
        </div>
        <el-divider />
        <el-form ref="avatarFormRef" :model="avatarForm" :rules="avatarRules" label-width="80px">
          <el-form-item label="上传头像">
            <el-upload
              class="avatar-upload"
              :show-file-list="false"
              accept="image/*"
              :http-request="handleAvatarUpload"
            >
              <el-button type="primary" plain>选择图片</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="头像URL" prop="avatar">
            <el-input v-model="avatarForm.avatar" placeholder="https://example.com/avatar.jpg" clearable />
          </el-form-item>
          <el-button type="primary" :loading="avatarLoading" @click="handleUpdateAvatar">更新头像</el-button>
        </el-form>
      </section>

      <section class="card info-card">
        <h2>基本信息</h2>
        <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">保存信息</el-button>
        </el-form>
      </section>

      <section class="card password-card">
        <h2>安全设置</h2>
        <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="90px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
            修改密码
          </el-button>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateUserInfo, updatePassword, updateAvatar } from '@/api/auth'
import { uploadAvatar } from '@/api/file'

const router = useRouter()
const userStore = useUserStore()

const infoFormRef = ref(null)
const avatarFormRef = ref(null)
const passwordFormRef = ref(null)

const infoLoading = ref(false)
const avatarLoading = ref(false)
const passwordLoading = ref(false)

const infoForm = reactive({
  nickname: '',
  email: '',
  phone: ''
})

const avatarForm = reactive({
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateEmail = (rule, value, callback) => {
  if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1\d{10}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const infoRules = {
  email: [{ validator: validateEmail, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }]
}

const avatarRules = {
  avatar: [{ required: true, message: '请输入头像URL', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const avatarPreview = computed(() => avatarForm.avatar || userStore.avatar || '')

const syncUserInfo = () => {
  infoForm.nickname = userStore.userInfo.nickname || ''
  infoForm.email = userStore.userInfo.email || ''
  infoForm.phone = userStore.userInfo.phone || ''
  avatarForm.avatar = userStore.userInfo.avatar || ''
}

const handleUpdateInfo = async () => {
  if (!infoFormRef.value) return
  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return
    infoLoading.value = true
    try {
      const res = await updateUserInfo(infoForm)
      ElMessage.success(res.message || '更新成功')
      await userStore.fetchUserInfo()
      syncUserInfo()
    } catch (error) {
      console.error('更新信息失败:', error)
    } finally {
      infoLoading.value = false
    }
  })
}

const handleUpdateAvatar = async () => {
  if (!avatarFormRef.value) return
  await avatarFormRef.value.validate(async (valid) => {
    if (!valid) return
    avatarLoading.value = true
    try {
      const res = await updateAvatar(avatarForm.avatar)
      ElMessage.success(res.message || '头像更新成功')
      await userStore.fetchUserInfo()
      syncUserInfo()
    } catch (error) {
      console.error('更新头像失败:', error)
    } finally {
      avatarLoading.value = false
    }
  })
}

const handleAvatarUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  avatarLoading.value = true
  try {
    const res = await uploadAvatar(formData)
    avatarForm.avatar = res.data
    const updateRes = await updateAvatar(res.data)
    ElMessage.success(updateRes.message || '头像更新成功')
    await userStore.fetchUserInfo()
    syncUserInfo()
    if (options.onSuccess) {
      options.onSuccess(res.data)
    }
  } catch (error) {
    if (options.onError) {
      options.onError(error)
    }
    console.error('上传头像失败:', error)
  } finally {
    avatarLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    passwordLoading.value = true
    try {
      const res = await updatePassword(passwordForm)
      ElMessage.success(res.message || '密码修改成功，请重新登录')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      await ElMessageBox.confirm('密码已修改，请重新登录。', '提示', {
        confirmButtonText: '去登录',
        cancelButtonText: '稍后',
        type: 'warning'
      }) 
      userStore.clearUserInfo()
      router.push('/login')
    } catch (error) {
      console.error('修改密码失败:', error)
    } finally {
      passwordLoading.value = false
    }
  })
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logout()
  } catch {
    // 取消退出
  }
}

onMounted(async () => {
  try {
    await userStore.fetchUserInfo()
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
  syncUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f7f8fc;
  padding: 32px;
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.header-left h1 {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 6px;
}

.header-left p {
  color: #6b7280;
  font-size: 14px;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
}

.avatar-card {
  grid-column: span 1;
}

.info-card,
.password-card {
  grid-column: span 1;
}

.avatar-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
}

.meta .name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.meta .sub {
  font-size: 13px;
  color: #6b7280;
  margin: 6px 0 8px;
}

.card h2 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #111827;
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
