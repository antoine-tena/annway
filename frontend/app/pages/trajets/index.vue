<script setup lang="ts">
import { computed, ref } from 'vue'
import TripCard from '~/components/trip/TripCard.vue'
import TripCriteriaFields from '~/components/trip/TripCriteriaFields.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import { errorMessage, type FormFields } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'
import { useTrips } from '~/composables/useTrips'
import { criteriaFields, emptyCriteria } from '~/utils/criteria'
import { eurosToCents } from '~/utils/format'

const trips = useTrips()
const { me } = useAuth()
const criteria = ref(emptyCriteria())
const maxPrice = ref('')
const earliest = ref('')
const latest = ref('')
const query = ref<FormFields>({})

const { data, status, error, refresh } = await useAsyncData('trip-search', () => trips.search(query.value), { watch: [query] })
const places = computed(() => data.value?.places ?? { departures: [], arrivals: [] })
const results = computed(() => data.value?.trips ?? [])
const isFiltered = computed(() => Object.keys(query.value).length > 0)
const maxPriceError = computed(() => maxPrice.value && eurosToCents(maxPrice.value) === null ? 'Un montant en euros, par exemple 7,50.' : '')

function search(): void {
  if (maxPriceError.value) {
    return
  }
  query.value = {
    ...criteriaFields(criteria.value),
    maxPriceCents: maxPrice.value ? eurosToCents(maxPrice.value) : null,
    earliest: earliest.value || null,
    latest: latest.value || null,
  }
}

function showAll(): void {
  criteria.value = emptyCriteria()
  maxPrice.value = ''
  earliest.value = ''
  latest.value = ''
  query.value = {}
}
</script>

<template>
  <div class="mx-auto max-w-page px-(--gutter-page) py-12">
    <UiPageHeader
      kicker="Catalogue"
      title="Trouver un trajet"
      subtitle="Sans critère, tous les trajets à venir. Avec départ, arrivée et date, les trajets qui ont assez de places."
    />

    <form
      class="mb-10 space-y-4 rounded-panel border border-line bg-surface-raised p-5"
      @submit.prevent="search"
    >
      <TripCriteriaFields
        v-model="criteria"
        :places="places"
      />
      <div class="grid gap-4 sm:grid-cols-3">
        <UiInput
          v-model="maxPrice"
          label="Prix maximum par place (€)"
          inputmode="decimal"
          placeholder="10"
          :error="maxPriceError"
        />
        <UiInput
          v-model="earliest"
          label="Départ après"
          type="time"
        />
        <UiInput
          v-model="latest"
          label="Départ avant"
          type="time"
        />
      </div>
      <div class="flex flex-wrap gap-3">
        <UiButton type="submit">
          Rechercher
        </UiButton>
        <UiButton
          v-if="isFiltered"
          variant="outline"
          @click="showAll"
        >
          Tous les trajets
        </UiButton>
      </div>
    </form>

    <UiAsyncState
      :is-loading="status === 'pending' && !data"
      :error="error ? errorMessage(error) : ''"
      :is-empty="results.length === 0"
      empty-text="Aucun trajet ne correspond. Créez une alerte ou une demande : les conducteurs la verront."
      @retry="refresh()"
    >
      <ul class="space-y-3">
        <li
          v-for="trip in results"
          :key="trip.id"
        >
          <TripCard :trip="trip" />
        </li>
      </ul>
    </UiAsyncState>
    <p
      v-if="me?.role === 'PASSENGER' && status !== 'pending'"
      class="mt-6 text-sm text-fg-muted"
    >
      Rien ne convient ?
      <NuxtLink
        to="/compte/alertes"
        class="font-semibold text-accent-fg hover:underline"
      >Créer une alerte ou une demande de trajet</NuxtLink>.
    </p>
  </div>
</template>
