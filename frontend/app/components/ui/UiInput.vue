<script setup lang="ts">
import { computed, useId } from 'vue'
import { fieldClasses, LABEL_CLASSES } from '~/utils/ui'

/**
 * Champ libellé ([ACCESSIBILITY]) : le `<label>` est relié au champ, l'erreur annoncée par
 * `aria-describedby`. Les autres attributs (`required`, `min`, `autocomplete`…) vont au champ.
 */
defineOptions({ inheritAttrs: false })

const { label, type = 'text', error = '', hint = '' } = defineProps<{
  label: string
  type?: string
  error?: string
  hint?: string
}>()

const model = defineModel<string | number>({ default: '' })

const inputId = `input-${useId()}`
const describedBy = computed(() => [error ? `${inputId}-error` : '', hint ? `${inputId}-hint` : ''].filter(Boolean).join(' ') || undefined)
</script>

<template>
  <div class="space-y-1.5">
    <label
      :for="inputId"
      :class="LABEL_CLASSES"
    >{{ label }}</label>
    <input
      :id="inputId"
      v-model="model"
      v-bind="$attrs"
      :type="type"
      :class="fieldClasses(Boolean(error))"
      :aria-invalid="error ? 'true' : undefined"
      :aria-describedby="describedBy"
    >
    <p
      v-if="hint"
      :id="`${inputId}-hint`"
      class="text-caption text-fg-muted"
    >
      {{ hint }}
    </p>
    <p
      v-if="error"
      :id="`${inputId}-error`"
      class="text-caption text-danger"
    >
      {{ error }}
    </p>
  </div>
</template>
