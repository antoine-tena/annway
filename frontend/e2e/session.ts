import { expect, type Browser, type Page } from '@playwright/test'

const DEMO_PASSWORD = process.env.E2E_DEMO_PASSWORD ?? ''

/** Connecte un compte de démonstration dans un contexte de navigateur à lui. */
export async function logIn(browser: Browser, email: string): Promise<Page> {
  const context = await browser.newContext()
  const page = await context.newPage()
  await page.goto('/connexion')
  await page.getByLabel('E-mail').fill(email)
  await page.getByLabel('Mot de passe').fill(DEMO_PASSWORD)
  await page.getByRole('button', { name: 'Se connecter' }).click()
  await expect(page.getByRole('button', { name: 'Déconnexion' })).toBeVisible()
  return page
}
