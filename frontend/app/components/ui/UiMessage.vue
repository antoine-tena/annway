<script setup lang="ts">
import { TONE_CLASSES } from '~/utils/ui'

/** Bandeau d'erreur ou de confirmation, annoncé aux lecteurs d'écran ; rien sans message. */
const { message, tone = 'danger' } = defineProps<{
  message: string
  tone?: 'danger' | 'success'
}>()
</script>

<template>
  <p
    v-if="message"
    :role="tone === 'danger' ? 'alert' : 'status'"
    class="rounded-control border px-4 py-3 text-sm"
    :class="TONE_CLASSES[tone]"
  >
    {{ message }}
  </p>
</template>
