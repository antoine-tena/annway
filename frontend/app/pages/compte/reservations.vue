<script setup lang="ts">
import { computed } from 'vue'
import ReservationCard from '~/components/reservation/ReservationCard.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'
import { useReservations } from '~/composables/useReservations'
import type { Reservation } from '~/types/api'

definePageMeta({ middleware: 'auth' })

const { me } = useAuth()
const reservations = useReservations()
const { data, status, error, refresh } = await useAsyncData('my-reservations', () => reservations.mine())
const isDriver = computed(() => me.value?.role === 'DRIVER')

function replace(updated: Reservation): void {
  data.value = (data.value ?? []).map(reservation => reservation.id === updated.id ? updated : reservation)
}
</script>

<template>
  <div class="mx-auto max-w-4xl px-(--gutter-page) py-12">
    <UiPageHeader
      :kicker="isDriver ? 'Conducteur' : 'Passager'"
      :title="isDriver ? 'Réservations reçues' : 'Mes réservations'"
      :subtitle="isDriver
        ? 'Les demandes de places sur vos trajets. Un trajet vers la station se valide une fois payé.'
        : 'Vos demandes de places. Payez un trajet vers la station pour que le conducteur puisse le valider.'"
    />
    <UiAsyncState
      :is-loading="status === 'pending' && !data"
      :error="error ? errorMessage(error) : ''"
      :is-empty="(data ?? []).length === 0"
      :empty-text="isDriver ? 'Aucune demande sur vos trajets pour l\'instant.' : 'Aucune réservation : trouvez un trajet dans le catalogue.'"
      @retry="refresh()"
    >
      <ul class="space-y-4">
        <li
          v-for="reservation in data"
          :key="reservation.id"
        >
          <ReservationCard
            :reservation="reservation"
            :role="me?.role ?? 'PASSENGER'"
            @changed="replace"
          />
        </li>
      </ul>
    </UiAsyncState>
  </div>
</template>
