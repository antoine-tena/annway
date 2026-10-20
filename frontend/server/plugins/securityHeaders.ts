import { getRequestURL, setResponseHeader, setResponseHeaders } from 'h3'
import {
  API_PATH_PREFIX,
  SECURITY_HEADERS,
  addNonceToScripts,
  contentSecurityPolicy,
  createNonce,
} from '../utils/securityHeaders'

export default defineNitroPlugin((nitroApp) => {
  nitroApp.hooks.hook('request', (event) => {
    if (!getRequestURL(event).pathname.startsWith(API_PATH_PREFIX)) {
      setResponseHeaders(event, SECURITY_HEADERS)
    }
  })

  // En développement, Vite injecte ses styles et son rechargement à chaud en ligne : la
  // politique ne s'applique qu'au front construit.
  if (import.meta.dev) {
    return
  }
  nitroApp.hooks.hook('render:html', (html, { event }) => {
    const nonce = createNonce()
    html.head = html.head.map((chunk) => addNonceToScripts(chunk, nonce))
    html.bodyPrepend = html.bodyPrepend.map((chunk) => addNonceToScripts(chunk, nonce))
    html.body = html.body.map((chunk) => addNonceToScripts(chunk, nonce))
    html.bodyAppend = html.bodyAppend.map((chunk) => addNonceToScripts(chunk, nonce))
    setResponseHeader(event, 'Content-Security-Policy', contentSecurityPolicy(nonce))
  })
})
