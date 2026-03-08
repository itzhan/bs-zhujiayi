<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NInput, NForm, NFormItem, NCard, useMessage } from 'naive-ui'
import { PersonOutline, LockClosedOutline, CallOutline, MailOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
  email: ''
})
const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.password) {
    message.warning('请填写用户名和密码')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    message.warning('两次密码输入不一致')
    return
  }
  if (form.value.password.length < 6) {
    message.warning('密码长度不能少于6位')
    return
  }

  loading.value = true
  try {
    await userStore.registerAction({
      username: form.value.username,
      password: form.value.password,
      nickname: form.value.nickname || form.value.username,
      phone: form.value.phone,
      email: form.value.email
    })
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (e: any) {
    message.error(e.message || '注册失败')
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
          <h2>加入我们</h2>
          <p>创建账户，享受专属会员优惠</p>
        </div>
      </div>
      <div class="auth-right">
        <NCard :bordered="false" class="auth-card">
          <h1 class="auth-title">用户注册</h1>
          <NForm class="auth-form" @submit.prevent="handleRegister">
            <NFormItem label="用户名" :show-feedback="false">
              <NInput
                v-model:value="form.username"
                placeholder="请输入用户名"
                size="large"
              >
                <template #prefix>
                  <PersonOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NFormItem label="昵称" :show-feedback="false" style="margin-top: 16px">
              <NInput
                v-model:value="form.nickname"
                placeholder="请输入昵称（选填）"
                size="large"
              />
            </NFormItem>
            <NFormItem label="密码" :show-feedback="false" style="margin-top: 16px">
              <NInput
                v-model:value="form.password"
                type="password"
                show-password-on="click"
                placeholder="请输入密码（至少6位）"
                size="large"
              >
                <template #prefix>
                  <LockClosedOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NFormItem label="确认密码" :show-feedback="false" style="margin-top: 16px">
              <NInput
                v-model:value="form.confirmPassword"
                type="password"
                show-password-on="click"
                placeholder="请再次输入密码"
                size="large"
              >
                <template #prefix>
                  <LockClosedOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NFormItem label="手机号" :show-feedback="false" style="margin-top: 16px">
              <NInput
                v-model:value="form.phone"
                placeholder="请输入手机号（选填）"
                size="large"
              >
                <template #prefix>
                  <CallOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NFormItem label="邮箱" :show-feedback="false" style="margin-top: 16px">
              <NInput
                v-model:value="form.email"
                placeholder="请输入邮箱（选填）"
                size="large"
              >
                <template #prefix>
                  <MailOutline style="font-size: 18px; color: #94A3B8" />
                </template>
              </NInput>
            </NFormItem>
            <NButton
              type="primary"
              block
              size="large"
              :loading="loading"
              style="margin-top: 24px"
              @click="handleRegister"
            >
              注 册
            </NButton>
          </NForm>
          <div class="auth-footer">
            已有账户？
            <router-link to="/login">立即登录</router-link>
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
  margin-bottom: 28px;
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
