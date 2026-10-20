<script setup lang="ts">
import type { Tone } from '~/utils/constants'
import { TONE_CLASSES } from '~/utils/ui'

/** Pastille d'état : le texte dit l'état, la teinte ne fait que l'appuyer. */
const { label, tone } = defineProps<{ label: string, tone: Tone }>()
</script>

<template>
  <span
    class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-label uppercase"
    :class="TONE_CLASSES[tone]"
  >{{ label }}</span>
</template>
