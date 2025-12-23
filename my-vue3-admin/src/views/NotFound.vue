<template>
  <div class="not-found">
    <div class="content">
      <div class="error-code">404</div>
      <h1>页面不存在</h1>
      <p>抱歉，您访问的页面不存在或已被删除</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="goHome">
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const goHome = () => {
  if (userStore.isAdmin) {
    router.push('/admin/home')
  } else if (userStore.isLoggedIn) {
    router.push('/home')
  } else {
    router.push('/login')
  }
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  text-align: center;
  color: #fff;
}

.error-code {
  font-size: 160px;
  font-weight: 900;
  line-height: 1;
  opacity: 0.3;
  margin-bottom: -30px;
}

h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

p {
  font-size: 16px;
  opacity: 0.8;
  margin-bottom: 32px;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.actions .el-button {
  padding: 12px 32px;
  border-radius: 24px;
}

@media (max-width: 640px) {
  .error-code {
    font-size: 100px;
  }
  
  h1 {
    font-size: 28px;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>

