// [NO-MAGIC-VALUES]: texts, thresholds, statuses, colours and delays of the product, named once.
import type { EquipmentType, ReservationStatus, Role, TripStatus, TripType } from '~/types/api'

/** Fuseau de tout affichage de date ou d'heure ([TIME-ZONE]). */
export const CALENDAR_ZONE = 'Europe/Paris'
export const LOCALE = 'fr-FR'
export const CURRENCY = 'EUR'
export const CENTS_PER_EURO = 100
export const MIN_PASSWORD_LENGTH = 10
export const MAX_MESSAGE_LENGTH = 1000
export const MIN_RATING = 1
export const MAX_RATING = 5

export const ROLE_LABELS: Record<Role, string> = {
  PASSENGER: 'Passager',
  DRIVER: 'Conducteur',
}

export const TRIP_TYPE_LABELS: Record<TripType, string> = {
  STATION: 'Vers la station',
  POLYTECH: 'Navette Polytech',
}

export const TRIP_STATUS_LABELS: Record<TripStatus, string> = {
  OPEN: 'Places libres',
  FULL: 'Complet',
  FINISHED: 'Terminé',
  CANCELLED: 'Annulé',
}

export const RESERVATION_STATUS_LABELS: Record<ReservationStatus, string> = {
  PENDING: 'En attente',
  VALIDATED: 'Validée',
  REFUSED: 'Refusée',
  CANCELLED: 'Annulée',
  TRIP_CANCELLED: 'Trajet annulé',
}

/** Délai avant le départ jusqu'auquel le passager qui annule est remboursé (`Reservation`). */
export const FREE_CANCELLATION_NOTICE_HOURS = 24

export const EQUIPMENT_LABELS: Record<EquipmentType, string> = {
  SKI: 'Skis',
  SNOWBOARD: 'Snowboard',
  SLED: 'Luge',
}

/** Teinte d'une pastille d'état, par jeton sémantique. */
export type Tone = 'accent' | 'success' | 'warning' | 'danger' | 'muted'

export const TRIP_STATUS_TONES: Record<TripStatus, Tone> = {
  OPEN: 'success',
  FULL: 'warning',
  FINISHED: 'muted',
  CANCELLED: 'danger',
}

export const RESERVATION_STATUS_TONES: Record<ReservationStatus, Tone> = {
  PENDING: 'warning',
  VALIDATED: 'success',
  REFUSED: 'danger',
  CANCELLED: 'muted',
  TRIP_CANCELLED: 'danger',
}

export const LOADING_TEXT = 'Chargement…'
export const NETWORK_ERROR = 'Le serveur ne répond pas : vérifiez votre connexion, ou lancez l\'API (backend/).'
