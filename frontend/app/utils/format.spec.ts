import { afterEach, describe, expect, it, vi } from 'vitest'
import { eurosToCents, formatCents, formatDay, formatShortDate, formatTime, todayInParis } from '~/utils/format'

// Intl sépare les milliers et l'unité par des espaces insécables : on les ramène à des espaces.
function plain(text: string): string {
  return text.replace(/\s/g, ' ')
}

describe('montants ([MONEY])', () => {
  it('affiche des centimes en euros', () => {
    expect(plain(formatCents(750))).toBe('7,50 €')
    expect(plain(formatCents(123456))).toBe('1 234,56 €')
  })

  it('convertit une saisie en euros en centimes entiers', () => {
    expect(eurosToCents('7,50')).toBe(750)
    expect(eurosToCents(' 3 ')).toBe(300)
    expect(eurosToCents('0.1')).toBe(10)
    expect(eurosToCents('19.99')).toBe(1999)
  })

  it('refuse ce qui n\'est pas un montant', () => {
    for (const typed of ['', 'abc', '-3', '1,234', '1.2.3', '7,5€']) {
      expect(eurosToCents(typed), typed).toBeNull()
    }
  })
})

describe('dates à l\'heure de Paris ([TIME-ZONE])', () => {
  afterEach(() => {
    vi.useRealTimers()
  })

  it('montre l\'heure de Paris, en hiver comme en été', () => {
    expect(formatTime('2026-12-16T06:30:00Z')).toBe('07:30')
    expect(formatTime('2026-07-16T06:30:00Z')).toBe('08:30')
  })

  it('un instant tard le soir en UTC est déjà le lendemain à Paris', () => {
    expect(formatDay('2027-01-14T23:30:00Z')).toBe('vendredi 15 janvier')
  })

  it('aujourd\'hui est compté à Paris', () => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2027-01-14T23:30:00Z'))
    expect(todayInParis()).toBe('2027-01-15')
  })

  it('une date calendaire garde son jour', () => {
    expect(formatShortDate('2026-12-16')).toBe('16 déc. 2026')
  })
})
