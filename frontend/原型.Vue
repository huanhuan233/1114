<!--
  工艺智能体平台原型 - 入口说明
  完整原型在 src 目录：运行 npm install 后执行 npm run dev 启动。
  功能模块：工艺知识库、CAPP/CAM/CAD 集成、工艺路径输出、权限管理。
-->
<template>
  <div class="proto-tip">
    <p>工艺智能体平台原型已创建在 <code>src/</code> 目录。</p>
    <p>请在本项目根目录执行：</p>
    <pre>npm install\nnpm run dev</pre>
    <p>在浏览器中打开控制台提示的地址即可查看完整原型。</p>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.proto-tip {
  padding: 24px;
  font-family: sans-serif;
  color: #303133;
}
.proto-tip code, .proto-tip pre {
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}
.proto-tip pre {
  display: inline-block;
  padding: 8px 16px;
  margin: 8px 0;
}
</style>
