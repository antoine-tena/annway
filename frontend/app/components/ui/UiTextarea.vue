<script setup lang="ts">
import { useId } from 'vue'
import { fieldClasses, LABEL_CLASSES } from '~/utils/ui'

/** Zone de texte libellée ([ACCESSIBILITY]). */
defineOptions({ inheritAttrs: false })

const { label } = defineProps<{ label: string }>()

const model = defineModel<string>({ default: '' })
const textareaId = `textarea-${useId()}`
</script>

<template>
  <div class="space-y-1.5">
    <label
      :for="textareaId"
      :class="LABEL_CLASSES"
    >{{ label }}</label>
    <textarea
      :id="textareaId"
      v-model="model"
      v-bind="$attrs"
      rows="3"
      :class="fieldClasses(false)"
    />
  </div>
</template>
