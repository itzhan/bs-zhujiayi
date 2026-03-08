<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  NCard, NButton, NInput, NForm, NFormItem, NAvatar, NUpload,
  NDivider, NTabs, NTabPane, useMessage
} from 'naive-ui'
import type { UploadFileInfo } from 'naive-ui'
import { PersonOutline, MailOutline, CallOutline, LockClosedOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'
import { updateUserProfile, changePassword } from '@/api/auth'
import { uploadFile } from '@/api/user'

const message = useMessage()
const userStore = useUserStore()

const profileForm = ref({
  nickname: '',
  phone: '',
  email: '',
  avatar: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loading = ref(false)
const pwdLoading = ref(false)

function initForm() {
  const info = userStore.userInfo
  if (info) {
    profileForm.value = {
      nickname: info.nickname || '',
      phone: info.phone || '',
      email: info.email || '',
      avatar: info.avatar || ''
    }
  }
}

async function handleSaveProfile() {
  loading.value = true
  try {
    await updateUserProfile(profileForm.value)
    await userStore.fetchProfile()
    message.success('个人信息已更新')
  } catch (e: any) {
    message.error(e.message || '更新失败')
  } finally {
    loading.value = false
  }
}

async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    message.warning('请填写完整密码信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.warning('两次密码输入不一致')
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    message.warning('新密码长度不能少于6位')
    return
  }

  pwdLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    message.success('密码已修改')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e: any) {
    message.error(e.message || '修改密码失败')
  } finally {
    pwdLoading.value = false
  }
}

async function handleAvatarUpload({ file }: { file: UploadFileInfo }) {
  if (!file.file) return
  try {
    const res: any = await uploadFile(file.file)
    profileForm.value.avatar = res.data
    message.success('头像已上传')
  } catch (e) {
    message.error('上传失败')
  }
}

onMounted(() => {
  userStore.fetchProfile().then(initForm).catch(initForm)
  initForm()
})
</script>

<template>
  <div class="profile-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px; max-width: 800px">
      <h1 class="page-title">个人中心</h1>

      <NTabs type="line" animated>
        <NTabPane name="profile" tab="基本信息">
          <NCard :bordered="true" size="medium" style="margin-top: 16px">
            <div class="avatar-section">
              <NAvatar
                :size="80"
                round
                :src="profileForm.avatar || undefined"
                :style="{ backgroundColor: '#1B4965', fontSize: '2rem' }"
              >
                {{ userStore.username.charAt(0).toUpperCase() }}
              </NAvatar>
              <NUpload
                :show-file-list="false"
                @change="handleAvatarUpload"
                accept="image/*"
              >
                <NButton size="small" tertiary>更换头像</NButton>
              </NUpload>
            </div>

            <NDivider />

            <NForm label-placement="left" label-width="80">
              <NFormItem label="用户名">
                <NInput :value="userStore.userInfo?.username" disabled />
              </NFormItem>
              <NFormItem label="昵称">
                <NInput v-model:value="profileForm.nickname" placeholder="请输入昵称" />
              </NFormItem>
              <NFormItem label="手机号">
                <NInput v-model:value="profileForm.phone" placeholder="请输入手机号">
                  <template #prefix>
                    <CallOutline style="font-size: 16px; color: #94A3B8" />
                  </template>
                </NInput>
              </NFormItem>
              <NFormItem label="邮箱">
                <NInput v-model:value="profileForm.email" placeholder="请输入邮箱">
                  <template #prefix>
                    <MailOutline style="font-size: 16px; color: #94A3B8" />
                  </template>
                </NInput>
              </NFormItem>
              <NFormItem>
                <NButton type="primary" :loading="loading" @click="handleSaveProfile">
                  保存修改
                </NButton>
              </NFormItem>
            </NForm>
          </NCard>
        </NTabPane>

        <NTabPane name="password" tab="修改密码">
          <NCard :bordered="true" size="medium" style="margin-top: 16px">
            <NForm label-placement="left" label-width="100">
              <NFormItem label="当前密码">
                <NInput
                  v-model:value="passwordForm.oldPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入当前密码"
                >
                  <template #prefix>
                    <LockClosedOutline style="font-size: 16px; color: #94A3B8" />
                  </template>
                </NInput>
              </NFormItem>
              <NFormItem label="新密码">
                <NInput
                  v-model:value="passwordForm.newPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入新密码（至少6位）"
                >
                  <template #prefix>
                    <LockClosedOutline style="font-size: 16px; color: #94A3B8" />
                  </template>
                </NInput>
              </NFormItem>
              <NFormItem label="确认新密码">
                <NInput
                  v-model:value="passwordForm.confirmPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请再次输入新密码"
                >
                  <template #prefix>
                    <LockClosedOutline style="font-size: 16px; color: #94A3B8" />
                  </template>
                </NInput>
              </NFormItem>
              <NFormItem>
                <NButton type="primary" :loading="pwdLoading" @click="handleChangePassword">
                  修改密码
                </NButton>
              </NFormItem>
            </NForm>
          </NCard>
        </NTabPane>
      </NTabs>
    </div>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--color-text-dark);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}
</style>
