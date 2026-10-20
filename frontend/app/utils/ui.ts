// Classes partagées des boutons, pour un <button> (UiButton) comme pour un lien qui en a l'air.
// Couleurs par les jetons de tokens.css, jamais en dur ; capitales du site public d'annski.
import type { Tone } from '~/utils/constants'

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
export type ButtonSize = 'sm' | 'md'

const BUTTON_VARIANTS: Record<ButtonVariant, string> = {
  primary: 'border border-accent bg-accent text-on-accent hover:border-accent-fg hover:bg-accent-hover',
  secondary: 'border border-line bg-fg/10 text-fg hover:bg-fg/20',
  outline: 'border border-line-strong text-fg-muted hover:text-fg hover:border-fg/60',
  ghost: 'text-fg-muted hover:text-fg',
  danger: 'border border-danger/30 bg-danger-soft text-danger hover:bg-danger/20',
}

const BUTTON_SIZES: Record<ButtonSize, string> = {
  sm: 'px-3 py-1.5 text-xs',
  md: 'px-5 py-2.5 text-sm',
}

export function buttonClasses(variant: ButtonVariant = 'primary', size: ButtonSize = 'md'): string {
  return [
    'inline-flex items-center justify-center gap-2 rounded-control font-display font-bold uppercase tracking-label',
    'transition-colors focus:outline-none focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent-fg',
    'disabled:cursor-not-allowed disabled:opacity-50',
    BUTTON_VARIANTS[variant],
    BUTTON_SIZES[size],
  ].join(' ')
}

export const TONE_CLASSES: Record<Tone, string> = {
  accent: 'border-accent/40 bg-accent-soft text-accent-fg',
  success: 'border-success/30 bg-success-soft text-success',
  warning: 'border-warning/30 bg-warning-soft text-warning',
  danger: 'border-danger/30 bg-danger-soft text-danger',
  muted: 'border-line bg-fg/5 text-fg-muted',
}

/** Contour et fond d'un champ de formulaire, selon qu'il est en erreur. */
export function fieldClasses(hasError: boolean): string {
  return [
    'block w-full rounded-control border bg-surface-raised px-3 py-2 text-sm text-fg placeholder-fg-muted',
    'transition-colors focus:outline-none focus:ring-2',
    hasError ? 'border-danger focus:ring-danger/30' : 'border-line-strong focus:border-accent-fg focus:ring-accent/30',
  ].join(' ')
}

export const LABEL_CLASSES = 'block text-label uppercase text-fg-muted'
export const CARD_CLASSES = 'rounded-card border border-line bg-surface-raised p-5'
