<script setup lang="ts">
import type { NuxtError } from '#app'
import { buttonClasses } from '~/utils/ui'

const NOT_FOUND = 404

const { error } = defineProps<{ error: NuxtError }>()
</script>

<template>
  <div class="flex min-h-dvh flex-col items-center justify-center gap-6 bg-surface px-(--gutter-page) text-center font-sans text-fg">
    <p class="text-kicker uppercase text-accent-fg">
      Erreur {{ error.statusCode }}
    </p>
    <h1 class="font-display text-display-md font-bold uppercase leading-display">
      {{ error.statusCode === NOT_FOUND ? 'Cette page n\'existe pas' : 'Quelque chose s\'est mal passé' }}
    </h1>
    <button
      type="button"
      :class="buttonClasses('primary')"
      @click="clearError({ redirect: '/' })"
    >
      Retour à l'accueil
    </button>
  </div>
</template>
