<script setup lang="ts">
import { ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'

const emit = defineEmits<{ done: [] }>()

const { login } = useAuth()
const email = ref('')
const password = ref('')
const isSending = ref(false)
const error = ref('')

async function submit(): Promise<void> {
  isSending.value = true
  error.value = ''
  try {
    await login(email.value, password.value)
    emit('done')
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <form
    class="space-y-4"
    @submit.prevent="submit"
  >
    <UiInput
      v-model="email"
      label="E-mail"
      type="email"
      autocomplete="email"
      required
    />
    <UiInput
      v-model="password"
      label="Mot de passe"
      type="password"
      autocomplete="current-password"
      required
    />
    <UiMessage :message="error" />
    <UiButton
      type="submit"
      class="w-full"
      :loading="isSending"
    >
      Se connecter
    </UiButton>
  </form>
</template>
