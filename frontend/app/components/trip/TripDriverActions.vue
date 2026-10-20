<script setup lang="ts">
import { ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useTrips } from '~/composables/useTrips'
import type { TripDetail } from '~/types/api'
import { CARD_CLASSES } from '~/utils/ui'

/** Gestion de son trajet par le conducteur : ajouter une étape, annuler, déclarer terminé. */
const { trip } = defineProps<{ trip: TripDetail }>()
const emit = defineEmits<{ changed: [] }>()

const trips = useTrips()
const place = ref('')
const passingAt = ref('')
const pendingAction = ref('')
const error = ref('')

async function run(action: string, call: () => Promise<unknown>): Promise<void> {
  pendingAction.value = action
  error.value = ''
  try {
    await call()
    emit('changed')
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    pendingAction.value = ''
  }
}

async function addStop(): Promise<void> {
  await run('stop', () => trips.addStop(trip.id, { place: place.value, passingAt: passingAt.value }))
  if (!error.value) {
    place.value = ''
    passingAt.value = ''
  }
}

function cancel(): void {
  if (window.confirm('Annuler ce trajet ? Les passagers ne pourront plus réserver.')) {
    run('cancel', () => trips.cancel(trip.id))
  }
}
</script>

<template>
  <section :class="[CARD_CLASSES, 'space-y-4']">
    <h2 class="font-display text-xl font-bold uppercase">
      Mon trajet
    </h2>
    <form
      class="grid gap-3 sm:grid-cols-[1fr_1fr_auto] sm:items-end"
      @submit.prevent="addStop"
    >
      <UiInput
        v-model="place"
        label="Étape"
        placeholder="Thônes"
        required
      />
      <UiInput
        v-model="passingAt"
        label="Passage (heure de Paris)"
        type="datetime-local"
        required
      />
      <UiButton
        type="submit"
        variant="secondary"
        :loading="pendingAction === 'stop'"
      >
        Ajouter
      </UiButton>
    </form>
    <UiMessage :message="error" />
    <div class="flex flex-wrap gap-3 border-t border-line pt-4">
      <UiButton
        variant="outline"
        :loading="pendingAction === 'finish'"
        :disabled="trip.status === 'FINISHED' || trip.status === 'CANCELLED'"
        @click="run('finish', () => trips.finish(trip.id))"
      >
        Trajet terminé
      </UiButton>
      <UiButton
        variant="danger"
        :loading="pendingAction === 'cancel'"
        :disabled="trip.status === 'FINISHED' || trip.status === 'CANCELLED'"
        @click="cancel"
      >
        Annuler le trajet
      </UiButton>
    </div>
  </section>
</template>
