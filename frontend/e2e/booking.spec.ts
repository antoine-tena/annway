import { expect, test } from '@playwright/test'
import { logIn } from './session'

// Trajet de démonstration n° 1 : Annecy → La Clusaz, conduit par Camille, 7 € la place.
const LA_CLUSAZ_TRIP = '/trajets/1'

test('le passager réserve et paie, le conducteur valide', async ({ browser }) => {
  const passenger = await logIn(browser, 'lea@example.org')
  await passenger.goto(LA_CLUSAZ_TRIP)
  await passenger.getByRole('button', { name: 'Demander une place' }).click()
  await expect(passenger.getByText('Demande envoyée')).toBeVisible()
  await passenger.goto('/compte/reservations')
  await passenger.getByRole('button', { name: /^Payer/ }).click()
  await expect(passenger.getByText(/Payé 7,00/)).toBeVisible()

  const driver = await logIn(browser, 'camille@example.org')
  await driver.goto('/compte/reservations')
  await driver.getByRole('button', { name: 'Valider' }).click()
  await expect(driver.getByText('Validée')).toBeVisible()

  await passenger.reload()
  await expect(passenger.getByText('Validée')).toBeVisible()
})
