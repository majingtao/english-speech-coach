<script setup lang="ts">
useHead({
  title: 'English Speech Coach',
  meta: [
    { name: 'description', content: 'Cambridge Flyers 口语练习' },
    { name: 'theme-color', content: '#ffffff' },
  ],
})
</script>

<template>
  <van-config-provider theme="light">
    <router-view />
  </van-config-provider>
</template>
