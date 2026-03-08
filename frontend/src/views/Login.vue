<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NButton, NInput, NForm, NFormItem, NCard, useMessage } from 'naive-ui'
import { LockClosedOutline, PersonOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.loginAction(form.value.username, form.value.password)
    message.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    message.error(e.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="auth-brand">
          <div class="brand-logo">
            <span class="logo-icon">H</span>
            <span class="logo-text">LuxStay</span>
          </div>
          <h2>欢迎回来</h2>
          <p>登录您的账户，开始预订之旅</p>
        </div>
      </div>
      <div class="auth-right">
        <NCard :bordered="false" class="auth-card">
          <h1 class="auth-title">用户登录</h1>
          <NForm class="auth-form" @submit.prevent="handleLogin">
            <NFormItem label="用户名" :show-feedback="false">
              <NInput
                v-model:value="form.username"
                placeholder="请输入用户名"
                size="large"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <PersonOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NFormItem label="密码" :show-feedback="false" style="margin-top: 20px">
              <NInput
                v-model:value="form.password"
                type="password"
                show-password-on="click"
                placeholder="请输入密码"
                size="large"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <LockClosedOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NButton
              type="primary"
              block
              size="large"
              :loading="loading"
              style="margin-top: 28px"
              @click="handleLogin"
            >
              登 录
            </NButton>
          </NForm>
          <div class="auth-footer">
            还没有账户？
            <router-link to="/register">立即注册</router-link>
          </div>
        </NCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9fc 0%, #eef2f7 50%, #f5f0eb 100%);
  padding: 40px 24px;
}

.auth-container {
  display: flex;
  width: 100%;
  max-width: 880px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
}

.auth-left {
  width: 360px;
  background: linear-gradient(160deg, #1B4965 0%, #2A7FA0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.auth-brand {
  text-align: center;
  color: #fff;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 32px;
}

.brand-logo .logo-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-weight: 800;
  font-size: 1.4rem;
  border-radius: 12px;
}

.brand-logo .logo-text {
  font-size: 1.5rem;
  font-weight: 700;
}

.auth-brand h2 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.auth-brand p {
  font-size: 0.9rem;
  opacity: 0.85;
}

.auth-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.auth-card {
  width: 100%;
  max-width: 400px;
}

.auth-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 32px;
  color: var(--color-text-dark);
}

.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 0.9rem;
  color: var(--color-text-medium);
}

.auth-footer a {
  color: var(--color-primary);
  font-weight: 600;
}

@media (max-width: 768px) {
  .auth-left {
    display: none;
  }
}
</style>
