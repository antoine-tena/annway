// Réservations : le passager demande, paie et annule ; le conducteur valide ou refuse.
import type { Reservation } from '~/types/api'
import { apiGet, apiPost } from '~/composables/useApiClient'

export function useReservations() {
  return {
    mine: () => apiGet<Reservation[]>('/api/me/reservations'),
    /** `equipment` : une entrée `TYPE:description` par article. */
    book: (tripId: number, seatCount: number, equipment: string[]) =>
      apiPost<Reservation>(`/api/trips/${tripId}/reservations`, { seatCount, equipment }),
    pay: (id: number, amountCents: number) => apiPost<Reservation>(`/api/reservations/${id}/pay`, { amountCents }),
    cancel: (id: number) => apiPost<Reservation>(`/api/reservations/${id}/cancel`),
    validate: (id: number) => apiPost<Reservation>(`/api/reservations/${id}/validate`),
    refuse: (id: number) => apiPost<Reservation>(`/api/reservations/${id}/refuse`),
  }
}
