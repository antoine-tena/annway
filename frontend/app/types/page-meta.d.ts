// Rôle exigé par une page, lu par le middleware `auth`.
import type { Role } from '~/types/api'

declare module '#app' {
  interface PageMeta {
    role?: Role
  }
}

export {}
