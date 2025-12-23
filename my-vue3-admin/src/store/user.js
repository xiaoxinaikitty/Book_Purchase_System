import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo.role === 1,
    username: (state) => state.userInfo.username || '',
    nickname: (state) => state.userInfo.nickname || state.userInfo.username || '',
    avatar: (state) => state.userInfo.avatar || ''
  },
  
  actions: {
    // 登录
    async login(loginForm) {
      try {
        const res = await login(loginForm)
        const { token, userId, username, nickname, avatar, role } = res.data
        
        // 保存 token 和用户信息
        this.token = token
        this.userInfo = { userId, username, nickname, avatar, role }
        
        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        
        return res
      } catch (error) {
        throw error
      }
    },
    
    // 退出登录
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('退出登录失败:', error)
      } finally {
        this.clearUserInfo()
        router.push('/login')
      }
    },
    
    // 清除用户信息
    clearUserInfo() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },
    
    // 获取用户信息
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = { ...this.userInfo, ...res.data }
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        return res
      } catch (error) {
        throw error
      }
    }
  }
})

