import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Gọi /api/... sẽ được chuyển sang backend ở cổng 8080,
// nhờ vậy trình duyệt coi như cùng một địa chỉ, cookie refresh token hoạt động bình thường
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
