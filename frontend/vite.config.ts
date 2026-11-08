import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Las peticiones a /api se reenvían al backend para evitar problemas de CORS
    proxy: {
      '/api': process.env.API_URL ?? 'http://localhost:8080',
    },
  },
})
