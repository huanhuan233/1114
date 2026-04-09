<template>
  <div class="page process-path">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>工艺路径输出</span>
          <el-button type="primary" :icon="Plus" @click="pathRef?.openDialog()">生成工艺路径</el-button>
        </div>
      </template>
      <ProcessPathEmbed ref="pathRef" :show-header="false" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import ProcessPathEmbed from '@/components/ProcessPathEmbed.vue'

const pathRef = ref<InstanceType<typeof ProcessPathEmbed> | null>(null)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
