<script setup lang="ts">
import { computed, useId } from 'vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiSelect from '~/components/ui/UiSelect.vue'
import type { TripType } from '~/types/api'
import { TRIP_TYPE_LABELS } from '~/utils/constants'
import type { Criteria } from '~/utils/criteria'

/**
 * Critères d'une recherche, d'une alerte ou d'une demande de trajet : départ, arrivée, date,
 * passagers, type. Les lieux connus sont proposés en suggestions, la saisie reste libre.
 */
const { places = { departures: [], arrivals: [] }, isRequired = true } = defineProps<{
  places?: { departures: string[], arrivals: string[] }
  isRequired?: boolean
}>()

const criteria = defineModel<Criteria>({ required: true })

const typeOptions = computed(() => (Object.keys(TRIP_TYPE_LABELS) as TripType[])
  .map(type => ({ value: type, label: TRIP_TYPE_LABELS[type] })))
const listId = `places-${useId()}`
</script>

<template>
  <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
    <UiInput
      v-model="criteria.departure"
      label="Départ"
      placeholder="Annecy"
      :list="`${listId}-departures`"
      :required="isRequired"
    />
    <UiInput
      v-model="criteria.arrival"
      label="Arrivée"
      placeholder="La Clusaz"
      :list="`${listId}-arrivals`"
      :required="isRequired"
    />
    <UiInput
      v-model="criteria.date"
      label="Date"
      type="date"
      :required="isRequired"
    />
    <UiInput
      v-model.number="criteria.passengerCount"
      label="Passagers"
      type="number"
      min="1"
      max="8"
    />
    <UiSelect
      v-model="criteria.type"
      label="Type"
      placeholder="Tous"
      :options="typeOptions"
    />
    <datalist :id="`${listId}-departures`">
      <option
        v-for="place in places.departures"
        :key="place"
        :value="place"
      />
    </datalist>
    <datalist :id="`${listId}-arrivals`">
      <option
        v-for="place in places.arrivals"
        :key="place"
        :value="place"
      />
    </datalist>
  </div>
</template>
