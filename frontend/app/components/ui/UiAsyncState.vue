<script setup lang="ts">
import UiButton from '~/components/ui/UiButton.vue'
import { LOADING_TEXT } from '~/utils/constants'

/**
 * Les trois états d'un écran qui charge ([UI-STATES]) : chargement, erreur compréhensible avec
 * « Réessayer », liste vide. Le contenu (slot) n'est rendu que s'il y a des données.
 */
const { isLoading, error, isEmpty, emptyText } = defineProps<{
  isLoading: boolean
  error: string
  isEmpty: boolean
  emptyText: string
}>()

const emit = defineEmits<{ retry: [] }>()
</script>

<template>
  <div
    v-if="isLoading"
    class="space-y-3"
    aria-busy="true"
  >
    <p class="sr-only">
      {{ LOADING_TEXT }}
    </p>
    <div
      v-for="placeholder in 3"
      :key="placeholder"
      class="h-20 animate-pulse rounded-card bg-fg/5"
      aria-hidden="true"
    />
  </div>
  <div
    v-else-if="error"
    role="alert"
    class="flex flex-col items-start gap-3 rounded-card border border-danger/30 bg-danger-soft p-5 text-sm text-danger"
  >
    <p>{{ error }}</p>
    <UiButton
      variant="outline"
      size="sm"
      @click="emit('retry')"
    >
      Réessayer
    </UiButton>
  </div>
  <p
    v-else-if="isEmpty"
    class="rounded-card border border-dashed border-line-strong p-8 text-center text-sm text-fg-muted"
  >
    {{ emptyText }}
  </p>
  <slot v-else />
</template>
