import AxeBuilder from '@axe-core/playwright'
import { expect, test, type Page } from '@playwright/test'
import { logIn } from './session'

// [AUTOMATED-ACCESSIBILITY] : les pages principales passent axe-core, cible WCAG 2.2 AA.
const WCAG_TAGS = ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa', 'wcag22aa']
const PUBLIC_PAGES = ['/', '/trajets', '/trajets/1', '/connexion']
const PASSENGER_PAGES = ['/compte', '/compte/reservations', '/compte/alertes']
const DRIVER_PAGES = ['/demandes', '/compte/trajets', '/trajets/publier']

async function expectNoViolation(page: Page, path: string): Promise<void> {
  await page.goto(path)
  // Le front est rendu dans le navigateur : on attend le titre de la page avant l'audit.
  await expect(page.locator('main h1')).toBeVisible()
  const audit = await new AxeBuilder({ page }).withTags(WCAG_TAGS).analyze()
  expect(audit.violations, path).toEqual([])
}

for (const path of PUBLIC_PAGES) {
  test(`${path} est accessible`, async ({ page }) => {
    await expectNoViolation(page, path)
  })
}

test('les pages du passager sont accessibles', async ({ browser }) => {
  const page = await logIn(browser, 'lea@example.org')
  for (const path of PASSENGER_PAGES) {
    await expectNoViolation(page, path)
  }
})

test('les pages du conducteur sont accessibles', async ({ browser }) => {
  const page = await logIn(browser, 'camille@example.org')
  for (const path of DRIVER_PAGES) {
    await expectNoViolation(page, path)
  }
})
