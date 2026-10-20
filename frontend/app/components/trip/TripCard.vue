<script setup lang="ts">
import UiStatusPill from '~/components/ui/UiStatusPill.vue'
import type { Trip } from '~/types/api'
import { TRIP_STATUS_LABELS, TRIP_STATUS_TONES, TRIP_TYPE_LABELS } from '~/utils/constants'
import { formatCents, formatDay, formatTime } from '~/utils/format'

/** Carte d'un trajet dans une liste : trajet, jour et heure, places, prix, conducteur. */
const { trip } = defineProps<{ trip: Trip }>()
</script>

<template>
  <NuxtLink
    :to="`/trajets/${trip.id}`"
    class="group grid gap-4 rounded-card border border-line bg-surface-raised p-5 transition-colors hover:border-accent-fg/60 focus:outline-none focus-visible:outline-2 focus-visible:outline-accent-fg sm:grid-cols-[auto_1fr_auto] sm:items-center"
  >
    <div class="font-display text-3xl font-bold leading-none tabular-nums">
      {{ formatTime(trip.departureAt) }}
      <p class="mt-1 font-sans text-caption font-normal capitalize text-fg-muted">
        {{ formatDay(trip.departureAt) }}
      </p>
    </div>
    <div class="min-w-0 space-y-1">
      <p class="truncate text-lg font-semibold">
        {{ trip.departure }} <span
          class="text-accent-fg"
          aria-hidden="true"
        >→</span><span class="sr-only">vers</span> {{ trip.arrival }}
      </p>
      <p class="text-sm text-fg-muted">
        {{ TRIP_TYPE_LABELS[trip.type] }}
        <template v-if="trip.servedStation">
          · bus pour {{ trip.servedStation }}
        </template>
        · avec {{ trip.driver.firstName }}
        <template v-if="trip.driver.averageRating !== null">
          · ★ {{ trip.driver.averageRating }}
        </template>
      </p>
    </div>
    <div class="flex items-center justify-between gap-4 sm:flex-col sm:items-end">
      <p class="font-display text-2xl font-bold">
        {{ trip.isPaymentRequired ? formatCents(trip.pricePerSeatCents) : 'Don libre' }}
      </p>
      <div class="flex items-center gap-2 text-sm text-fg-muted">
        <span>{{ trip.availableSeats }}/{{ trip.seatCount }} places</span>
        <UiStatusPill
          :label="TRIP_STATUS_LABELS[trip.status]"
          :tone="TRIP_STATUS_TONES[trip.status]"
        />
      </div>
    </div>
  </NuxtLink>
</template>
