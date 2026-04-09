<template>
  <div class="page manufacturing-instruction">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>制造指令生成</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="工艺路径生成" name="path">
          <ProcessPathEmbed />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ProcessPathEmbed from '@/components/ProcessPathEmbed.vue'

const activeTab = ref('path')
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
