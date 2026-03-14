import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // Alle API-Calls an das Spring-Boot-Backend weiterleiten
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})

