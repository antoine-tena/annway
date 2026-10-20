<script setup lang="ts">
/** Titre de page à la manière d'annski : surtitre espacé, titre condensé en capitales. */
const { kicker = '', title, subtitle = '' } = defineProps<{
  kicker?: string
  title: string
  subtitle?: string
}>()
</script>

<template>
  <header class="mb-8 space-y-3">
    <p
      v-if="kicker"
      class="text-kicker uppercase text-accent-fg"
    >
      {{ kicker }}
    </p>
    <h1 class="font-display text-display-sm font-bold uppercase leading-display tracking-display">
      {{ title }}
    </h1>
    <p
      v-if="subtitle"
      class="max-w-prose text-fg-muted"
    >
      {{ subtitle }}
    </p>
    <slot />
  </header>
</template>
