// Alertes de trajet du passager et demandes de trajet, que les conducteurs consultent.
import type { TripAlert, TripRequest } from '~/types/api'
import { apiGet, apiPost, type FormFields } from '~/composables/useApiClient'

export function useAlerts() {
  return {
    alerts: () => apiGet<TripAlert[]>('/api/me/alerts'),
    subscribe: (criteria: FormFields) => apiPost<TripAlert>('/api/me/alerts', criteria),
    toggle: (index: number) => apiPost<TripAlert>(`/api/me/alerts/${index}/toggle`),
    myRequests: () => apiGet<TripRequest[]>('/api/me/trip-requests'),
    createRequest: (criteria: FormFields) => apiPost<TripRequest>('/api/me/trip-requests', criteria),
    allRequests: () => apiGet<TripRequest[]>('/api/trip-requests'),
  }
}
