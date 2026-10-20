import { randomBytes } from 'node:crypto'
import { defineConfig, devices } from '@playwright/test'

// Parcours de bout en bout ([E2E-JOURNEYS]) contre la pile complète : l'API Java et le front
// construit, sur des ports à eux pour ne pas croiser `kiln dev`.
const API_PORT = 8180
const FRONT_PORT = 3100
const SERVER_START_TIMEOUT_MS = 240_000
const DEMO_PASSWORD_BYTES = 12

// Mot de passe des comptes de démonstration (fictifs), tiré à chaque lancement ; les tests le
// relisent dans l'environnement.
process.env.E2E_DEMO_PASSWORD ??= randomBytes(DEMO_PASSWORD_BYTES).toString('base64url')

export default defineConfig({
  testDir: 'e2e',
  fullyParallel: false,
  workers: 1,
  forbidOnly: true,
  use: { baseURL: `http://localhost:${FRONT_PORT}`, trace: 'retain-on-failure' },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: [
    {
      command: './mvnw -q -B -DskipTests package && sh -c \'set -- target/*.jar && exec java -jar "$1"\'',
      cwd: '../backend',
      env: { ANNWAY_PORT: String(API_PORT), ANNWAY_DEMO_PASSWORD: process.env.E2E_DEMO_PASSWORD },
      url: `http://localhost:${API_PORT}/api/health`,
      timeout: SERVER_START_TIMEOUT_MS,
    },
    {
      command: 'pnpm build && node .output/server/index.mjs',
      env: { ANNWAY_API_ORIGIN: `http://localhost:${API_PORT}`, PORT: String(FRONT_PORT) },
      url: `http://localhost:${FRONT_PORT}`,
      timeout: SERVER_START_TIMEOUT_MS,
    },
  ],
})
