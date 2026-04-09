<script setup lang="ts">
useHead({
  title: 'English Speech Coach',
  meta: [
    { name: 'description', content: 'Cambridge Flyers 口语练习' },
    {
      name: 'theme-color',
      content: () => isDark.value ? '#0B0A0A' : '#ffffff',
    },
  ],
})

const mode = computed(() => isDark.value ? 'dark' : 'light')
</script>

<template>
  <van-config-provider :theme="mode">
    <router-view />
  </van-config-provider>
</template>
