// Trajets : recherche, détail, publication et vie d'un trajet, messages de son annonce.
import type { EquipmentTypeInfo, Trip, TripDetail, TripMessage, TripSearchResult } from '~/types/api'
import { apiGet, apiPost, type FormFields } from '~/composables/useApiClient'

export function useTrips() {
  return {
    search: (query: FormFields = {}) => apiGet<TripSearchResult>('/api/trips', query),
    detail: (id: number) => apiGet<TripDetail>(`/api/trips/${id}`),
    mine: () => apiGet<Trip[]>('/api/me/trips'),
    publish: (fields: FormFields) => apiPost<TripDetail>('/api/trips', fields),
    addStop: (id: number, fields: FormFields) => apiPost<TripDetail>(`/api/trips/${id}/stops`, fields),
    cancel: (id: number) => apiPost<TripDetail>(`/api/trips/${id}/cancel`),
    finish: (id: number) => apiPost<TripDetail>(`/api/trips/${id}/finish`),
    messages: (id: number) => apiGet<TripMessage[]>(`/api/trips/${id}/messages`),
    sendMessage: (id: number, content: string) => apiPost<TripMessage>(`/api/trips/${id}/messages`, { content }),
    equipmentTypes: () => apiGet<EquipmentTypeInfo[]>('/api/equipment-types'),
  }
}
