// Liens de l'en-tête selon le rôle du compte connecté (aucun compte : le catalogue seul).
import type { Role } from '~/types/api'

export interface NavigationLink {
  to: string
  label: string
}

const CATALOGUE: NavigationLink = { to: '/trajets', label: 'Trajets' }

const LINKS_BY_ROLE: Record<Role, NavigationLink[]> = {
  PASSENGER: [
    CATALOGUE,
    { to: '/compte/reservations', label: 'Mes réservations' },
    { to: '/compte/alertes', label: 'Alertes et demandes' },
  ],
  DRIVER: [
    CATALOGUE,
    { to: '/trajets/publier', label: 'Publier' },
    { to: '/compte/trajets', label: 'Mes trajets' },
    { to: '/compte/reservations', label: 'Réservations reçues' },
    { to: '/demandes', label: 'Demandes' },
  ],
}

export function navigationLinks(role: Role | null): NavigationLink[] {
  return role ? LINKS_BY_ROLE[role] : [CATALOGUE]
}
