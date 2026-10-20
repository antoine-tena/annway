// Charge le compte connecté au démarrage, pour que l'en-tête sache qui est là. Un serveur
// injoignable laisse la page anonyme : chaque écran signale lui-même son erreur de chargement.
import { useAuth } from '~/composables/useAuth'

export default defineNuxtPlugin(async () => {
  const { refresh } = useAuth()
  try {
    await refresh()
  } catch (error: unknown) {
    console.warn('compte non chargé', error)
  }
})
