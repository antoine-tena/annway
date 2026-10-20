<script setup lang="ts">
import { ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAccount } from '~/composables/useAccount'
import type { Me, VerificationChannel } from '~/types/api'

const { channel, label } = defineProps<{ channel: VerificationChannel, label: string }>()
const emit = defineEmits<{ verified: [me: Me], failed: [message: string] }>()

const account = useAccount()
const code = ref('')
const demoCode = ref('')
const isPending = ref(false)

async function run(call: () => Promise<void>): Promise<void> {
  isPending.value = true
  try {
    await call()
  } catch (caught: unknown) {
    emit('failed', errorMessage(caught))
  } finally {
    isPending.value = false
  }
}

const requestCode = () => run(async () => {
  demoCode.value = (await account.requestCode(channel)).demoCode
})

const confirmCode = () => run(async () => {
  emit('verified', await account.confirmCode(channel, code.value))
})
</script>

<template>
  <form
    v-if="demoCode"
    class="space-y-2"
    @submit.prevent="confirmCode"
  >
    <UiInput
      v-model="code"
      :label="`Code reçu (${label})`"
      :hint="`Démo : le code est ${demoCode}.`"
      inputmode="numeric"
      autocomplete="one-time-code"
      required
    />
    <UiButton
      type="submit"
      :loading="isPending"
    >
      Valider
    </UiButton>
  </form>
  <UiButton
    v-else
    variant="outline"
    :loading="isPending"
    @click="requestCode"
  >
    Vérifier : {{ label }}
  </UiButton>
</template>
