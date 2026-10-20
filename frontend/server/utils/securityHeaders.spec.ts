import { describe, expect, it } from 'vitest'
import { SECURITY_HEADERS, addNonceToScripts, contentSecurityPolicy, createNonce } from './securityHeaders'

describe('[SECURITY-HEADERS]', () => {
  it('envoie les en-têtes exigés', () => {
    expect(SECURITY_HEADERS['Strict-Transport-Security']).toContain('preload')
    expect(SECURITY_HEADERS['X-Content-Type-Options']).toBe('nosniff')
    expect(SECURITY_HEADERS['Referrer-Policy']).toBe('strict-origin-when-cross-origin')
    expect(SECURITY_HEADERS['Permissions-Policy']).toBeTruthy()
    expect(SECURITY_HEADERS['Cross-Origin-Opener-Policy']).toBe('same-origin')
  })
})

describe('[CSP-NONCE]', () => {
  it('ne permet ni inline, ni eval, ni joker', () => {
    const policy = contentSecurityPolicy(createNonce())
    expect(policy).not.toMatch(/unsafe-inline|unsafe-eval|\*/)
    expect(policy).toContain('frame-ancestors \'none\'')
  })

  it('autorise les scripts au nonce de la requête', () => {
    expect(contentSecurityPolicy('abc')).toContain('script-src \'self\' \'nonce-abc\'')
  })

  it('tire un nonce neuf à chaque requête', () => {
    expect(createNonce()).not.toBe(createNonce())
    expect(Buffer.from(createNonce(), 'base64')).toHaveLength(16)
  })

  it('pose le nonce sur chaque script de la page', () => {
    const html = '<script type="importmap">{}</script><script>window.__NUXT__={}</script>'
    expect(addNonceToScripts(html, 'n1'))
      .toBe('<script nonce="n1" type="importmap">{}</script><script nonce="n1">window.__NUXT__={}</script>')
  })
})
