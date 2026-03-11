<template>
  <div class="page home-page">
    <h1 class="home-title">航空轻量化构件制造工艺智能设计软件系统</h1>
    <div class="home-banner">
      <img :src="bannerImg" alt="航空轻量化构件制造工艺智能设计软件系统" class="banner-img" />
    </div>
    <div class="home-cards">
      <router-link
        v-for="item in portalItems"
        :key="item.path"
        :to="item.path"
        class="home-card"
      >
        <span class="home-card-label">{{ item.label }}</span>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import bannerImg from '../../image/images.jpg'

const portalItems = [
  { label: '知识库', path: '/knowledge/portal' },
  { label: '可制造性分析与优化软件', path: '/integration/process-review' },
  { label: '特种工艺决策与指令生成软件', path: '/integration/capp/special' },
  { label: '复材成型工艺高精度仿真软件', path: '/integration/cae/process' },
  { label: '任务管理', path: '/integration' },
  { label: '智能体管理', path: '/agent-config' },
  { label: '系统管理', path: '/system' },
]
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 24px 48px;
  background: #e8f4fd;
}
.home-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px 0;
}
.home-banner {
  width: 100%;
  max-width: 1200px;
  margin-bottom: 28px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.banner-img {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
  max-height: 280px;
}
.home-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  width: 100%;
  max-width: 1200px;
  margin-top: auto;
}
.home-card {
  aspect-ratio: 1.4;
  min-height: 72px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(64, 158, 255, 0.25);
  box-shadow: 0 0 16px rgba(64, 158, 255, 0.2), 0 0 0 1px rgba(64, 158, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #303133;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s ease;
}
.home-card:hover {
  color: #409eff;
  box-shadow: 0 0 24px rgba(64, 158, 255, 0.35), 0 0 0 2px rgba(64, 158, 255, 0.25);
  transform: translateY(-2px);
}
.home-card-label {
  text-align: center;
  padding: 0 12px;
}
</style>
