// Client de l'API Java, servie sous /api/ par le mandataire de Nuxt (même origine : le cookie de
// session suit tout seul). Les écritures partent en formulaire, les réponses arrivent en JSON.
// Les pages n'appellent jamais ce client : elles passent par le composable de leur domaine.
import type { ApiErrorBody } from '~/types/api'
import { NETWORK_ERROR } from '~/utils/constants'

export type FormValue = string | number | boolean | string[] | null | undefined
export type FormFields = Record<string, FormValue>

/** Erreur rendue par l'API : code machine, message en français et champ fautif. */
export class ApiError extends Error {
  readonly status: number
  readonly code: string
  readonly field: string | null
  readonly data: ApiErrorBody

  constructor(status: number, body: ApiErrorBody) {
    super(body.message)
    this.status = status
    this.code = body.code
    this.field = body.field
    this.data = body
  }
}

function isErrorBody(value: unknown): value is ApiErrorBody {
  return typeof value === 'object' && value !== null
    && typeof (value as { code?: unknown }).code === 'string'
    && typeof (value as { message?: unknown }).message === 'string'
}

/** Champs d'un formulaire en corps `application/x-www-form-urlencoded` ; une liste se répète. */
export function toFormBody(fields: FormFields): URLSearchParams {
  const body = new URLSearchParams()
  for (const [name, value] of Object.entries(fields)) {
    if (value === null || value === undefined || value === '') {
      continue
    }
    for (const single of Array.isArray(value) ? value : [value]) {
      body.append(name, String(single))
    }
  }
  return body
}

async function request<T>(path: string, options: { method: 'GET' | 'POST', query?: FormFields, body?: FormFields }): Promise<T> {
  try {
    return await $fetch<T>(path, {
      method: options.method,
      query: options.query ? Object.fromEntries(toFormBody(options.query)) : undefined,
      body: options.body ? toFormBody(options.body) : undefined,
    })
  } catch (error: unknown) {
    const response = (error as { response?: { status?: number, _data?: unknown } }).response
    if (response && isErrorBody(response._data)) {
      throw new ApiError(response.status ?? 0, response._data)
    }
    throw new Error(NETWORK_ERROR, { cause: error })
  }
}

export function apiGet<T>(path: string, query?: FormFields): Promise<T> {
  return request<T>(path, { method: 'GET', query })
}

export function apiPost<T>(path: string, body: FormFields = {}): Promise<T> {
  return request<T>(path, { method: 'POST', body })
}

/** Message à montrer pour une erreur, quelle que soit la couche qui l'a enveloppée. */
export function errorMessage(error: unknown): string {
  if (error instanceof Error) {
    if (error.cause instanceof ApiError) {
      return error.cause.message
    }
    const data = (error as { data?: unknown }).data
    if (isErrorBody(data)) {
      return data.message
    }
    return error.message || NETWORK_ERROR
  }
  return NETWORK_ERROR
}

/** Vrai si l'erreur est un refus faute de session (401). */
export function isUnauthenticated(error: unknown): boolean {
  return error instanceof ApiError && error.status === 401
}
