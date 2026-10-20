<script setup lang="ts">
import { computed } from 'vue'
import { buttonClasses, type ButtonSize, type ButtonVariant } from '~/utils/ui'

/**
 * Bouton du produit, sur le modèle du bouton sombre d'annski. `loading` montre l'attente (roue
 * et bouton inactif) sans changer le libellé.
 */
const { type = 'button', variant = 'primary', size = 'md', disabled = false, loading = false } = defineProps<{
  type?: 'button' | 'submit'
  variant?: ButtonVariant
  size?: ButtonSize
  disabled?: boolean
  loading?: boolean
}>()

const classes = computed(() => buttonClasses(variant, size))
</script>

<template>
  <button
    :type="type"
    :class="classes"
    :disabled="disabled || loading"
    :aria-busy="loading ? 'true' : undefined"
  >
    <svg
      v-if="loading"
      class="h-4 w-4 animate-spin"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <circle
        class="opacity-25"
        cx="12"
        cy="12"
        r="10"
        stroke="currentColor"
        stroke-width="4"
      />
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
      />
    </svg>
    <slot />
  </button>
</template>
