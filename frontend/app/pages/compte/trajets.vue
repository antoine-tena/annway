<script setup lang="ts">
import TripCard from '~/components/trip/TripCard.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useTrips } from '~/composables/useTrips'
import { buttonClasses } from '~/utils/ui'

definePageMeta({ middleware: 'auth', role: 'DRIVER' })

const trips = useTrips()
const { data, status, error, refresh } = await useAsyncData('my-trips', () => trips.mine())
</script>

<template>
  <div class="mx-auto max-w-page px-(--gutter-page) py-12">
    <UiPageHeader
      kicker="Conducteur"
      title="Mes trajets"
      subtitle="Ouvrez un trajet pour ajouter une étape, l'annuler ou le déclarer terminé."
    >
      <NuxtLink
        to="/trajets/publier"
        :class="buttonClasses('primary')"
      >
        Publier un trajet
      </NuxtLink>
    </UiPageHeader>
    <UiAsyncState
      :is-loading="status === 'pending' && !data"
      :error="error ? errorMessage(error) : ''"
      :is-empty="(data ?? []).length === 0"
      empty-text="Vous n'avez publié aucun trajet."
      @retry="refresh()"
    >
      <ul class="space-y-3">
        <li
          v-for="trip in data"
          :key="trip.id"
        >
          <TripCard :trip="trip" />
        </li>
      </ul>
    </UiAsyncState>
  </div>
</template>
