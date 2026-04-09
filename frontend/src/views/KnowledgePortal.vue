<template>
  <div class="page knowledge-portal">
    <iframe
      class="portal-iframe"
      src="http://192.168.0.172/web/#/home"
      title="知识库门户"
      frameborder="0"
    />
  </div>
</template>

<script setup lang="ts">
// 工艺知识库外链：iframe 嵌入，不跳转
</script>

<style scoped>
.knowledge-portal {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  margin: -20px;
  padding: 0;
  background: #fff;
}
.portal-iframe {
  flex: 1;
  width: 100%;
  min-height: 0;
  border: none;
  display: block;
}
</style>
