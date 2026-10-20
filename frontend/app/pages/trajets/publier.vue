<script setup lang="ts">
import { computed, ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import UiSelect from '~/components/ui/UiSelect.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'
import { useTrips } from '~/composables/useTrips'
import type { TripType } from '~/types/api'
import { TRIP_TYPE_LABELS } from '~/utils/constants'
import { eurosToCents } from '~/utils/format'

definePageMeta({ middleware: 'auth', role: 'DRIVER' })

const { me } = useAuth()
const trips = useTrips()
const type = ref<TripType>('STATION')
const departure = ref('Annecy')
const arrival = ref('')
const departureAt = ref('')
const registration = ref(me.value?.vehicles[0]?.registration ?? '')
const price = ref('')
const busDepartureAt = ref('')
const servedStation = ref('')
const isSending = ref(false)
const error = ref('')

const typeOptions = (Object.keys(TRIP_TYPE_LABELS) as TripType[]).map(value => ({ value, label: TRIP_TYPE_LABELS[value] }))
const vehicleOptions = computed(() => (me.value?.vehicles ?? [])
  .map(vehicle => ({ value: vehicle.registration, label: `${vehicle.model} · ${vehicle.registration} · ${vehicle.seatCount} places` })))
const priceError = computed(() => price.value && eurosToCents(price.value) === null ? 'Un montant en euros, par exemple 7,50.' : '')

async function publish(): Promise<void> {
  if (priceError.value) {
    return
  }
  isSending.value = true
  error.value = ''
  try {
    const isStation = type.value === 'STATION'
    const trip = await trips.publish({
      type: type.value,
      departure: departure.value,
      arrival: arrival.value,
      departureAt: departureAt.value,
      registration: registration.value,
      pricePerSeatCents: isStation ? eurosToCents(price.value) : null,
      busDepartureAt: isStation ? null : busDepartureAt.value,
      servedStation: isStation ? null : servedStation.value,
    })
    await navigateTo(`/trajets/${trip.id}`)
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <div class="mx-auto max-w-3xl px-(--gutter-page) py-12">
    <UiPageHeader
      kicker="Conducteur"
      title="Publier un trajet"
      subtitle="Les places et la capacité de matériel viennent du véhicule choisi. Les heures sont celles de Paris."
    />
    <form
      class="space-y-5 rounded-panel border border-line bg-surface-raised p-6"
      @submit.prevent="publish"
    >
      <UiSelect
        v-model="type"
        label="Type de trajet"
        :options="typeOptions"
      />
      <div class="grid gap-4 sm:grid-cols-2">
        <UiInput
          v-model="departure"
          label="Départ"
          required
        />
        <UiInput
          v-model="arrival"
          :label="type === 'STATION' ? 'Station' : 'Arrêt du bus'"
          :placeholder="type === 'STATION' ? 'La Clusaz' : 'Gare routière d\'Annecy'"
          required
        />
      </div>
      <div class="grid gap-4 sm:grid-cols-2">
        <UiInput
          v-model="departureAt"
          label="Départ le"
          type="datetime-local"
          required
        />
        <UiSelect
          v-model="registration"
          label="Véhicule"
          :options="vehicleOptions"
          required
        />
      </div>
      <UiInput
        v-if="type === 'STATION'"
        v-model="price"
        label="Prix par place (€)"
        inputmode="decimal"
        placeholder="7,00"
        :error="priceError"
        required
      />
      <div
        v-else
        class="grid gap-4 sm:grid-cols-2"
      >
        <UiInput
          v-model="busDepartureAt"
          label="Départ du bus le"
          type="datetime-local"
          required
        />
        <UiInput
          v-model="servedStation"
          label="Station desservie par le bus"
          placeholder="La Clusaz"
          required
        />
      </div>
      <UiMessage :message="error" />
      <UiButton
        type="submit"
        :loading="isSending"
      >
        Publier le trajet
      </UiButton>
    </form>
  </div>
</template>
