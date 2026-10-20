// https://nuxt.com/docs/api/configuration/nuxt-config
import tailwindcss from '@tailwindcss/vite'

// Origine du serveur de l'API Java (backend/, `fr.annway.web.WebMain`). Le front la sert sous
// /api/ par mandataire : même origine, donc le cookie de session suit sans CORS.
const apiOrigin = process.env.ANNWAY_API_ORIGIN ?? 'http://localhost:8080'

export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  modules: ['@nuxt/eslint'],
  // Application connectée, sans référencement : rendue dans le navigateur, la session reste
  // dans son cookie HttpOnly sans passer par le serveur de rendu.
  ssr: false,
  // [EXPLICIT-IMPORTS]: the project's components are imported by hand.
  components: { dirs: [] },
  css: ['~/assets/css/main.css'],
  vite: { plugins: [tailwindcss()] },
  typescript: { strict: true, typeCheck: false },
  routeRules: { '/api/**': { proxy: `${apiOrigin}/api/**` } },
  app: {
    head: {
      htmlAttrs: { lang: 'fr' },
      title: 'annway',
      meta: [{ name: 'description', content: 'Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.' }],
    },
  },
})
