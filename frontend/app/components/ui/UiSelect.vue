<script setup lang="ts">
import { useId } from 'vue'
import { fieldClasses, LABEL_CLASSES } from '~/utils/ui'

/** Liste de choix libellée ([ACCESSIBILITY]) ; `placeholder` ajoute un choix vide en tête. */
defineOptions({ inheritAttrs: false })

const { label, options, placeholder = '' } = defineProps<{
  label: string
  options: { value: string, label: string }[]
  placeholder?: string
}>()

const model = defineModel<string>({ default: '' })
const selectId = `select-${useId()}`
</script>

<template>
  <div class="space-y-1.5">
    <label
      :for="selectId"
      :class="LABEL_CLASSES"
    >{{ label }}</label>
    <select
      :id="selectId"
      v-model="model"
      v-bind="$attrs"
      :class="fieldClasses(false)"
    >
      <option
        v-if="placeholder"
        value=""
      >
        {{ placeholder }}
      </option>
      <option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
      >
        {{ option.label }}
      </option>
    </select>
  </div>
</template>
