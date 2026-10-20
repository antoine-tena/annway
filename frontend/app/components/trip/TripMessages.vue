<script setup lang="ts">
import { ref } from 'vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiTextarea from '~/components/ui/UiTextarea.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useTrips } from '~/composables/useTrips'
import { MAX_MESSAGE_LENGTH } from '~/utils/constants'
import { formatDay, formatTime } from '~/utils/format'
import { CARD_CLASSES } from '~/utils/ui'

/** Questions et réponses sous l'annonce, entre membres connectés. */
const { tripId } = defineProps<{ tripId: number }>()

const trips = useTrips()
const content = ref('')
const isSending = ref(false)
const sendError = ref('')
const { data, status, error, refresh } = await useAsyncData(`trip-messages-${tripId}`, () => trips.messages(tripId))

async function send(): Promise<void> {
  isSending.value = true
  sendError.value = ''
  try {
    await trips.sendMessage(tripId, content.value)
    content.value = ''
    await refresh()
  } catch (caught: unknown) {
    sendError.value = errorMessage(caught)
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <section :class="[CARD_CLASSES, 'space-y-4']">
    <h2 class="font-display text-xl font-bold uppercase">
      Questions sur le trajet
    </h2>
    <UiAsyncState
      :is-loading="status === 'pending' && !data"
      :error="error ? errorMessage(error) : ''"
      :is-empty="(data ?? []).length === 0"
      empty-text="Pas encore de message. Une question sur le matériel ou le point de rendez-vous ?"
      @retry="refresh()"
    >
      <ul class="space-y-3">
        <li
          v-for="(message, index) in data"
          :key="index"
          class="rounded-control bg-fg/5 p-3"
        >
          <p class="text-caption text-fg-muted">
            <strong class="text-fg">{{ message.sender.firstName }}</strong>
            · {{ formatDay(message.sentAt) }} à {{ formatTime(message.sentAt) }}
          </p>
          <p class="mt-1 whitespace-pre-line text-sm">
            {{ message.content }}
          </p>
        </li>
      </ul>
    </UiAsyncState>
    <form
      class="space-y-3"
      @submit.prevent="send"
    >
      <UiTextarea
        v-model="content"
        label="Votre message"
        :maxlength="MAX_MESSAGE_LENGTH"
        required
      />
      <UiMessage :message="sendError" />
      <UiButton
        type="submit"
        variant="secondary"
        :loading="isSending"
      >
        Envoyer
      </UiButton>
    </form>
  </section>
</template>
