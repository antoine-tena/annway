// Garde des pages connectées : sans session, vers la connexion (avec retour) ; avec le mauvais
// rôle (`definePageMeta({ role: 'DRIVER' })`), vers le compte. L'API refuse de toute façon
// (401, 403) : la garde évite seulement d'afficher un écran inutilisable.
import type { Role } from '~/types/api'
import { useAuth } from '~/composables/useAuth'

export default defineNuxtRouteMiddleware(async (to) => {
  const { me, isLoaded, refresh } = useAuth()
  if (!isLoaded.value) {
    try {
      await refresh()
    } catch (error: unknown) {
      console.warn('compte non chargé', error)
    }
  }
  if (!me.value) {
    return navigateTo({ path: '/connexion', query: { retour: to.fullPath } })
  }
  const role = to.meta.role as Role | undefined
  if (role && me.value.role !== role) {
    return navigateTo('/compte')
  }
})
