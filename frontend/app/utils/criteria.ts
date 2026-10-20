// Critères saisis d'une recherche, d'une alerte ou d'une demande de trajet.
import type { FormFields } from '~/composables/useApiClient'
import type { SearchCriteria, TripType } from '~/types/api'
import { TRIP_TYPE_LABELS } from '~/utils/constants'
import { formatShortDate, todayInParis } from '~/utils/format'

export interface Criteria {
  departure: string
  arrival: string
  date: string
  passengerCount: number
  type: TripType | ''
}

export function emptyCriteria(): Criteria {
  return { departure: '', arrival: '', date: todayInParis(), passengerCount: 1, type: '' }
}

export function criteriaFields(criteria: Criteria): FormFields {
  return { ...criteria, type: criteria.type || null }
}

/** « Annecy → La Clusaz, 16 déc. 2026, 2 passagers, vers la station ». */
export function describeCriteria(criteria: SearchCriteria): string {
  const passengers = `${criteria.passengerCount} passager${criteria.passengerCount > 1 ? 's' : ''}`
  const parts = [`${criteria.departure} → ${criteria.arrival}`, formatShortDate(criteria.date), passengers]
  if (criteria.type) {
    parts.push(TRIP_TYPE_LABELS[criteria.type].toLowerCase())
  }
  return parts.join(', ')
}
