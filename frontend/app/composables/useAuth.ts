// Compte connecté : chargé une fois par `refresh()`, partagé par toutes les pages.
import type { Me } from '~/types/api'
import { apiGet, apiPost, isUnauthenticated, type FormFields } from '~/composables/useApiClient'

export function useAuth() {
  const me = useState<Me | null>('auth-me', () => null)
  const isLoaded = useState<boolean>('auth-loaded', () => false)

  async function refresh(): Promise<Me | null> {
    try {
      me.value = await apiGet<Me>('/api/me')
    } catch (error: unknown) {
      if (!isUnauthenticated(error)) {
        throw error
      }
      me.value = null
    } finally {
      isLoaded.value = true
    }
    return me.value
  }

  async function login(email: string, password: string): Promise<Me> {
    me.value = await apiPost<Me>('/api/auth/login', { email, password })
    return me.value
  }

  async function register(fields: FormFields): Promise<Me> {
    me.value = await apiPost<Me>('/api/auth/register', fields)
    return me.value
  }

  async function logout(): Promise<void> {
    await apiPost<unknown>('/api/auth/logout')
    me.value = null
  }

  return { me, isLoaded, refresh, login, register, logout }
}
