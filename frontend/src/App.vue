<script setup lang="ts">
import { NConfigProvider, NMessageProvider, NDialogProvider, NNotificationProvider } from 'naive-ui'
import type { GlobalThemeOverrides } from 'naive-ui'
import AppHeader from '@/components/Header.vue'
import AppFooter from '@/components/Footer.vue'

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#1B4965',
    primaryColorHover: '#2A7FA0',
    primaryColorPressed: '#153A52',
    primaryColorSuppl: '#1B4965',
    borderRadius: '6px',
    fontFamily: `-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif`
  },
  Button: {
    borderRadiusMedium: '8px',
    borderRadiusLarge: '10px'
  },
  Card: {
    borderRadius: '10px'
  },
  Input: {
    borderRadius: '8px'
  }
}
</script>

<template>
  <NConfigProvider :theme-overrides="themeOverrides">
    <NMessageProvider>
      <NDialogProvider>
        <NNotificationProvider>
          <div class="page-wrapper">
            <AppHeader />
            <main class="page-content">
              <router-view />
            </main>
            <AppFooter />
          </div>
        </NNotificationProvider>
      </NDialogProvider>
    </NMessageProvider>
  </NConfigProvider>
</template>
