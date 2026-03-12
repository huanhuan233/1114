<template>
  <div class="integration-layout">
    <router-view :key="route.fullPath" />
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.integration-layout {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.integration-layout :deep(> *) {
  flex: 1;
  min-height: 0;
}
</style>
