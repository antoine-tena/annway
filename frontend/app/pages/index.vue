<script setup lang="ts">
import { computed } from 'vue'
import TripCard from '~/components/trip/TripCard.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'
import { useTrips } from '~/composables/useTrips'
import { buttonClasses } from '~/utils/ui'

const HOME_TRIP_COUNT = 4

const { me } = useAuth()
const trips = useTrips()
const { data, status, error, refresh } = await useAsyncData('home-trips', () => trips.search())
const nextTrips = computed(() => (data.value?.trips ?? []).slice(0, HOME_TRIP_COUNT))
</script>

<template>
  <div>
    <section class="relative overflow-hidden border-b border-line bg-surface-sunken">
      <div
        class="pointer-events-none absolute -right-32 -top-32 h-96 w-96 rounded-full bg-accent/30 blur-3xl"
        aria-hidden="true"
      />
      <div class="relative mx-auto max-w-page px-(--gutter-page) py-20 sm:py-28">
        <p class="text-kicker uppercase text-accent-fg">
          Sorties annski · saison d'hiver
        </p>
        <h1 class="mt-4 max-w-3xl font-display text-display-lg font-bold uppercase leading-display tracking-display">
          Montez à la station <span class="text-accent-fg">ensemble</span>
        </h1>
        <p class="mt-6 max-w-xl text-lg text-fg-muted">
          Proposez les places libres de votre voiture, réservez la vôtre, skis et luge compris.
          Vers la station à prix fixe, ou jusqu'au bus depuis Polytech contre un don libre.
        </p>
        <div class="mt-8 flex flex-wrap gap-3">
          <NuxtLink
            to="/trajets"
            :class="buttonClasses('primary')"
          >
            Trouver un trajet
          </NuxtLink>
          <NuxtLink
            :to="me?.role === 'DRIVER' ? '/trajets/publier' : '/connexion?mode=inscription'"
            :class="buttonClasses('secondary')"
          >
            Proposer un trajet
          </NuxtLink>
        </div>
      </div>
    </section>

    <section class="mx-auto max-w-page px-(--gutter-page) py-14">
      <div class="mb-6 flex items-end justify-between gap-4">
        <h2 class="font-display text-display-sm font-bold uppercase leading-display">
          Prochains départs
        </h2>
        <NuxtLink
          to="/trajets"
          class="text-sm font-semibold text-accent-fg hover:underline"
        >
          Tous les trajets
        </NuxtLink>
      </div>
      <UiAsyncState
        :is-loading="status === 'pending' && !data"
        :error="error ? errorMessage(error) : ''"
        :is-empty="nextTrips.length === 0"
        empty-text="Aucun trajet à venir pour l'instant."
        @retry="refresh()"
      >
        <ul class="space-y-3">
          <li
            v-for="trip in nextTrips"
            :key="trip.id"
          >
            <TripCard :trip="trip" />
          </li>
        </ul>
      </UiAsyncState>
    </section>
  </div>
</template>
