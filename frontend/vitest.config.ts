import { fileURLToPath } from 'node:url'
import { defineConfig } from 'vitest/config'

// Tests unitaires des fonctions pures du front (`app/utils/`) et du serveur Nitro (`server/`).
export default defineConfig({
  resolve: { alias: { '~': fileURLToPath(new URL('./app', import.meta.url)) } },
  test: {
    include: ['app/**/*.spec.ts', 'server/**/*.spec.ts'],
    environment: 'node',
  },
})
