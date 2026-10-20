<script setup lang="ts">
import { computed } from 'vue'
import TripBookingForm from '~/components/trip/TripBookingForm.vue'
import TripDriverActions from '~/components/trip/TripDriverActions.vue'
import TripMessages from '~/components/trip/TripMessages.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiStatusPill from '~/components/ui/UiStatusPill.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'
import { useTrips } from '~/composables/useTrips'
import { TRIP_STATUS_LABELS, TRIP_STATUS_TONES, TRIP_TYPE_LABELS } from '~/utils/constants'
import { formatCents, formatDay, formatTime } from '~/utils/format'
import { buttonClasses, CARD_CLASSES } from '~/utils/ui'

const route = useRoute()
const tripId = Number(route.params.id)
const { me } = useAuth()
const trips = useTrips()
const { data: trip, status, error, refresh } = await useAsyncData(`trip-${tripId}`, () => trips.detail(tripId))

const isOwnTrip = computed(() => me.value?.id === trip.value?.driver.id)
const canBook = computed(() => me.value?.role === 'PASSENGER' && trip.value?.status === 'OPEN')
</script>

<template>
  <div class="mx-auto max-w-page px-(--gutter-page) py-12">
    <NuxtLink
      to="/trajets"
      class="text-sm font-semibold text-fg-muted hover:text-fg"
    >
      ← Tous les trajets
    </NuxtLink>
    <UiAsyncState
      :is-loading="status === 'pending' && !trip"
      :error="error ? errorMessage(error) : ''"
      :is-empty="!trip"
      empty-text="Ce trajet n'existe pas."
      @retry="refresh()"
    >
      <div
        v-if="trip"
        class="mt-6 grid gap-6 lg:grid-cols-[2fr_1fr]"
      >
        <div class="space-y-6">
          <header class="space-y-3">
            <div class="flex flex-wrap items-center gap-3">
              <p class="text-kicker uppercase text-accent-fg">
                {{ TRIP_TYPE_LABELS[trip.type] }}
              </p>
              <UiStatusPill
                :label="TRIP_STATUS_LABELS[trip.status]"
                :tone="TRIP_STATUS_TONES[trip.status]"
              />
            </div>
            <h1 class="font-display text-display-md font-bold uppercase leading-display tracking-display">
              {{ trip.departure }} <span class="text-accent-fg">→</span> {{ trip.arrival }}
            </h1>
            <p class="text-lg capitalize text-fg-muted">
              {{ formatDay(trip.departureAt) }} · départ {{ formatTime(trip.departureAt) }}
            </p>
          </header>

          <dl :class="[CARD_CLASSES, 'grid gap-4 sm:grid-cols-3']">
            <div>
              <dt class="text-label uppercase text-fg-muted">
                Prix par place
              </dt>
              <dd class="font-display text-2xl font-bold">
                {{ trip.isPaymentRequired ? formatCents(trip.pricePerSeatCents) : 'Don libre' }}
              </dd>
            </div>
            <div>
              <dt class="text-label uppercase text-fg-muted">
                Places libres
              </dt>
              <dd class="font-display text-2xl font-bold">
                {{ trip.availableSeats }} / {{ trip.seatCount }}
              </dd>
            </div>
            <div>
              <dt class="text-label uppercase text-fg-muted">
                Matériel
              </dt>
              <dd class="font-display text-2xl font-bold">
                {{ trip.remainingEquipmentCapacity }} unités
              </dd>
            </div>
          </dl>

          <section :class="CARD_CLASSES">
            <h2 class="mb-4 font-display text-xl font-bold uppercase">
              Itinéraire
            </h2>
            <ol class="space-y-3 border-l border-accent-fg/40 pl-5">
              <li>
                <strong>{{ formatTime(trip.departureAt) }}</strong> · {{ trip.departure }}
              </li>
              <li
                v-for="stop in trip.stops"
                :key="stop.place + stop.passingAt"
              >
                <strong>{{ formatTime(stop.passingAt) }}</strong> · {{ stop.place }}
              </li>
              <li>{{ trip.arrival }}</li>
              <li
                v-if="trip.busDepartureAt"
                class="text-fg-muted"
              >
                <strong class="text-fg">{{ formatTime(trip.busDepartureAt) }}</strong> · bus pour {{ trip.servedStation }}
              </li>
            </ol>
          </section>

          <TripMessages
            v-if="me"
            :trip-id="trip.id"
          />
        </div>

        <aside class="space-y-6">
          <section :class="[CARD_CLASSES, 'space-y-2']">
            <p class="text-label uppercase text-fg-muted">
              Conducteur
            </p>
            <p class="text-xl font-semibold">
              {{ trip.driver.firstName }}
              <span
                v-if="trip.driver.isVerified"
                class="ml-1 text-sm text-success"
              >✓ vérifié</span>
            </p>
            <p class="text-sm text-fg-muted">
              {{ trip.vehicleModel }}
              <template v-if="trip.driver.averageRating !== null">
                · ★ {{ trip.driver.averageRating }} ({{ trip.driver.reviewCount }} avis)
              </template>
            </p>
            <NuxtLink
              v-if="me && !isOwnTrip"
              :to="`/membres/${trip.driver.id}`"
              class="inline-block text-sm font-semibold text-accent-fg hover:underline"
            >
              Voir le profil
            </NuxtLink>
          </section>

          <TripDriverActions
            v-if="isOwnTrip"
            :trip="trip"
            @changed="refresh()"
          />
          <TripBookingForm
            v-else-if="canBook"
            :trip="trip"
            @booked="refresh()"
          />
          <div
            v-else-if="!me"
            :class="[CARD_CLASSES, 'space-y-3']"
          >
            <p class="text-sm text-fg-muted">
              Connectez-vous pour réserver une place ou poser une question.
            </p>
            <NuxtLink
              :to="{ path: '/connexion', query: { retour: route.fullPath } }"
              :class="buttonClasses('primary')"
            >
              Connexion
            </NuxtLink>
          </div>
        </aside>
      </div>
    </UiAsyncState>
  </div>
</template>
