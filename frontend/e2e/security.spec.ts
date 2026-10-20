import { expect, test } from '@playwright/test'

// [SECURITY-HEADERS] et [CSP-NONCE] sur le front construit, vus par le navigateur.
test('la page porte ses en-têtes et la CSP ne bloque rien', async ({ page }) => {
  const violations: string[] = []
  page.on('console', (message) => {
    if (message.text().includes('Content Security Policy')) {
      violations.push(message.text())
    }
  })
  const response = await page.goto('/')
  const headers = response?.headers() ?? {}
  expect(headers['strict-transport-security']).toContain('preload')
  expect(headers['x-content-type-options']).toBe('nosniff')
  expect(headers['cross-origin-opener-policy']).toBe('same-origin')
  expect(headers['content-security-policy']).toMatch(/script-src 'self' 'nonce-[\w+/=]+'/)
  await page.getByRole('link', { name: 'Trajets' }).first().click()
  await expect(page.locator('main')).toBeVisible()
  expect(violations).toEqual([])
})
