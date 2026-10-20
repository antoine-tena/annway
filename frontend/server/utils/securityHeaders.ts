import { randomBytes } from 'node:crypto'

// [SECURITY-HEADERS]: les mêmes valeurs que l'API Java (`fr.annway.web.HttpEntry`).
export const SECURITY_HEADERS: Readonly<Record<string, string>> = {
  'Strict-Transport-Security': 'max-age=63072000; includeSubDomains; preload',
  'X-Content-Type-Options': 'nosniff',
  'Referrer-Policy': 'strict-origin-when-cross-origin',
  'Permissions-Policy': 'camera=(), microphone=(), geolocation=()',
  'Cross-Origin-Opener-Policy': 'same-origin',
}

// L'API est servie par mandataire sous /api/ : elle envoie déjà ses propres en-têtes.
export const API_PATH_PREFIX = '/api/'

const NONCE_BYTES = 16

export function createNonce(): string {
  return randomBytes(NONCE_BYTES).toString('base64')
}

// [CSP-NONCE]: aucune origine tierce. Les fichiers de /_nuxt/ (scripts, styles, polices
// @fontsource) viennent de 'self' ; les seuls scripts en ligne, ceux que Nuxt écrit dans la
// page, portent le nonce de la requête.
export function contentSecurityPolicy(nonce: string): string {
  return [
    "default-src 'self'",
    `script-src 'self' 'nonce-${nonce}'`,
    "style-src 'self'",
    "img-src 'self' data:",
    "font-src 'self'",
    "connect-src 'self'",
    "object-src 'none'",
    "base-uri 'none'",
    "form-action 'self'",
    "frame-ancestors 'none'",
  ].join('; ')
}

export function addNonceToScripts(htmlChunk: string, nonce: string): string {
  return htmlChunk.replaceAll('<script', `<script nonce="${nonce}"`)
}
