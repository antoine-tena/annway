import { afterEach, describe, expect, it, vi } from 'vitest'
import { criteriaFields, describeCriteria, emptyCriteria } from '~/utils/criteria'

describe('critères de recherche', () => {
  afterEach(() => {
    vi.useRealTimers()
  })

  it('part d\'aujourd\'hui à Paris, pour un passager, sans type', () => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2027-01-14T23:30:00Z'))
    expect(emptyCriteria()).toEqual({ departure: '', arrival: '', date: '2027-01-15', passengerCount: 1, type: '' })
  })

  it('n\'envoie pas de type vide à l\'API', () => {
    const criteria = { departure: 'Annecy', arrival: 'La Clusaz', date: '2026-12-16', passengerCount: 2, type: '' as const }
    expect(criteriaFields(criteria).type).toBeNull()
    expect(criteriaFields({ ...criteria, type: 'STATION' }).type).toBe('STATION')
  })

  it('se décrit en une phrase', () => {
    const criteria = { departure: 'Annecy', arrival: 'La Clusaz', date: '2026-12-16', passengerCount: 2 }
    expect(describeCriteria({ ...criteria, type: null })).toBe('Annecy → La Clusaz, 16 déc. 2026, 2 passagers')
    expect(describeCriteria({ ...criteria, passengerCount: 1, type: 'STATION' }))
      .toBe('Annecy → La Clusaz, 16 déc. 2026, 1 passager, vers la station')
  })
})
