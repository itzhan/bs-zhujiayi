import { defineStore } from 'pinia'
import { login, register, getUserProfile } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null') as any
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.nickname || state.userInfo?.username || ''
  },
  actions: {
    async loginAction(username: string, password: string) {
      const res: any = await login({ username, password })
      this.token = res.data.token
      this.userInfo = res.data.user
      localStorage.setItem('token', this.token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },
    async registerAction(data: { username: string; password: string; nickname: string; phone: string; email: string }) {
      await register(data)
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },
    async fetchProfile() {
      const res: any = await getUserProfile()
      this.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    }
  }
})
