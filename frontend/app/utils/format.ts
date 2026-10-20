// Affichage des montants et des dates : centimes convertis en euros seulement ici ([MONEY]),
// instants UTC montrés à l'heure de Paris ([TIME-ZONE]).
import { CALENDAR_ZONE, CENTS_PER_EURO, CURRENCY, LOCALE } from '~/utils/constants'

const priceFormat = new Intl.NumberFormat(LOCALE, { style: 'currency', currency: CURRENCY })
const dayFormat = new Intl.DateTimeFormat(LOCALE, { timeZone: CALENDAR_ZONE, weekday: 'long', day: 'numeric', month: 'long' })
const timeFormat = new Intl.DateTimeFormat(LOCALE, { timeZone: CALENDAR_ZONE, hour: '2-digit', minute: '2-digit' })
const shortDateFormat = new Intl.DateTimeFormat(LOCALE, { timeZone: CALENDAR_ZONE, day: 'numeric', month: 'short', year: 'numeric' })
const isoDayFormat = new Intl.DateTimeFormat('en-CA', { timeZone: CALENDAR_ZONE, year: 'numeric', month: '2-digit', day: '2-digit' })

export function formatCents(cents: number): string {
  return priceFormat.format(cents / CENTS_PER_EURO)
}

/** Euros saisis (« 7,50 ») en centimes entiers ; `null` si la saisie n'est pas un montant. */
export function eurosToCents(euros: string): number | null {
  const normalized = euros.trim().replace(',', '.')
  if (!/^\d+(\.\d{1,2})?$/.test(normalized)) {
    return null
  }
  return Math.round(Number(normalized) * CENTS_PER_EURO)
}

export function formatDay(instant: string): string {
  return dayFormat.format(new Date(instant))
}

export function formatTime(instant: string): string {
  return timeFormat.format(new Date(instant))
}

/** Date calendaire (`2026-12-16`) en clair. */
export function formatShortDate(isoDate: string): string {
  return shortDateFormat.format(new Date(`${isoDate}T12:00:00Z`))
}

/** Aujourd'hui à Paris, au format `2026-12-16` des champs de date. */
export function todayInParis(): string {
  return isoDayFormat.format(new Date())
}
