// Profil du compte connecté, véhicules, profils publics et avis.
import type { Me, PublicProfile, VerificationChannel, VerificationCodeSent } from '~/types/api'
import { apiGet, apiPost, type FormFields } from '~/composables/useApiClient'

export function useAccount() {
  return {
    updateBio: (bio: string) => apiPost<Me>('/api/me/profile', { bio }),
    requestCode: (channel: VerificationChannel) =>
      apiPost<VerificationCodeSent>(`/api/me/verifications/${channel}`, {}),
    confirmCode: (channel: VerificationChannel, code: string) =>
      apiPost<Me>(`/api/me/verifications/${channel}/confirm`, { code }),
    addVehicle: (fields: FormFields) => apiPost<Me>('/api/me/vehicles', fields),
    reply: (index: number, reply: string) => apiPost<Me>(`/api/me/reviews/${index}/reply`, { reply }),
    profile: (id: number) => apiGet<PublicProfile>(`/api/users/${id}`),
    review: (id: number, rating: number, comment: string) =>
      apiPost<PublicProfile>(`/api/users/${id}/reviews`, { rating, comment }),
  }
}
