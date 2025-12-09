import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

//引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

//引入路由配置
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
