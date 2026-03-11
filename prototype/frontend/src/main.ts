import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局错误：避免单组件报错导致整页白屏，至少打印并可选提示
app.config.errorHandler = (err, instance, info) => {
  console.error('[Vue Error]', info, err)
}

// 路由/页面加载失败时自动刷新一次，避免点着点着白屏、只能手动刷新的情况
router.onError((err) => {
  console.error('[Router Error]', err)
  const win = typeof window !== 'undefined' ? window : null
  const key = '__routerErrorReload'
  if (win && !(win as unknown as Record<string, boolean>)[key]) {
    ;(win as unknown as Record<string, boolean>)[key] = true
    win.location.reload()
  }
})

app.use(ElementPlus).use(router).mount('#app')
