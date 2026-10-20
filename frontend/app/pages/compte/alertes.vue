<script setup lang="ts">
import { ref } from 'vue'
import TripCard from '~/components/trip/TripCard.vue'
import TripCriteriaFields from '~/components/trip/TripCriteriaFields.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import UiStatusPill from '~/components/ui/UiStatusPill.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAlerts } from '~/composables/useAlerts'
import { criteriaFields, describeCriteria, emptyCriteria } from '~/utils/criteria'
import { formatShortDate } from '~/utils/format'
import { CARD_CLASSES } from '~/utils/ui'

definePageMeta({ middleware: 'auth', role: 'PASSENGER' })

const alerts = useAlerts()
const criteria = ref(emptyCriteria())
const pending = ref('')
const error = ref('')
const success = ref('')

const alertList = await useAsyncData('my-alerts', () => alerts.alerts())
const requestList = await useAsyncData('my-trip-requests', () => alerts.myRequests())

async function create(kind: 'alert' | 'request'): Promise<void> {
  pending.value = kind
  error.value = ''
  success.value = ''
  try {
    if (kind === 'alert') {
      await alerts.subscribe(criteriaFields(criteria.value))
      await alertList.refresh()
      success.value = 'Alerte créée : les trajets publiés qui conviennent s\'y ajouteront.'
    } else {
      await alerts.createRequest(criteriaFields(criteria.value))
      await requestList.refresh()
      success.value = 'Demande envoyée : les conducteurs la voient dans leurs demandes.'
    }
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    pending.value = ''
  }
}

async function toggle(index: number): Promise<void> {
  try {
    await alerts.toggle(index)
    await alertList.refresh()
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  }
}
</script>

<template>
  <div class="mx-auto max-w-page px-(--gutter-page) py-12">
    <UiPageHeader
      kicker="Passager"
      title="Alertes et demandes"
      subtitle="Une alerte garde les trajets publiés qui correspondent à vos critères. Une demande dit aux conducteurs qu'un trajet manque."
    />
    <form
      :class="[CARD_CLASSES, 'mb-10 space-y-4']"
      @submit.prevent="create('alert')"
    >
      <TripCriteriaFields v-model="criteria" />
      <UiMessage :message="error" />
      <UiMessage
        :message="success"
        tone="success"
      />
      <div class="flex flex-wrap gap-3">
        <UiButton
          type="submit"
          :loading="pending === 'alert'"
        >
          Créer une alerte
        </UiButton>
        <UiButton
          variant="secondary"
          :loading="pending === 'request'"
          @click="create('request')"
        >
          Envoyer une demande
        </UiButton>
      </div>
    </form>

    <div class="grid gap-10 lg:grid-cols-2">
      <section class="space-y-4">
        <h2 class="font-display text-xl font-bold uppercase">
          Mes alertes
        </h2>
        <UiAsyncState
          :is-loading="alertList.status.value === 'pending' && !alertList.data.value"
          :error="alertList.error.value ? errorMessage(alertList.error.value) : ''"
          :is-empty="(alertList.data.value ?? []).length === 0"
          empty-text="Aucune alerte."
          @retry="alertList.refresh()"
        >
          <article
            v-for="alert in alertList.data.value"
            :key="alert.index"
            :class="[CARD_CLASSES, 'space-y-3']"
          >
            <div class="flex flex-wrap items-center justify-between gap-2">
              <p class="font-semibold">
                {{ describeCriteria(alert.criteria) }}
              </p>
              <UiStatusPill
                :label="alert.isActive ? 'Active' : 'En pause'"
                :tone="alert.isActive ? 'success' : 'muted'"
              />
            </div>
            <ul
              v-if="alert.matchedTrips.length"
              class="space-y-2"
            >
              <li
                v-for="trip in alert.matchedTrips"
                :key="trip.id"
              >
                <TripCard :trip="trip" />
              </li>
            </ul>
            <p
              v-else
              class="text-sm text-fg-muted"
            >
              Aucun trajet publié depuis sa création.
            </p>
            <UiButton
              variant="outline"
              size="sm"
              @click="toggle(alert.index)"
            >
              {{ alert.isActive ? 'Mettre en pause' : 'Réactiver' }}
            </UiButton>
          </article>
        </UiAsyncState>
      </section>

      <section class="space-y-4">
        <h2 class="font-display text-xl font-bold uppercase">
          Mes demandes
        </h2>
        <UiAsyncState
          :is-loading="requestList.status.value === 'pending' && !requestList.data.value"
          :error="requestList.error.value ? errorMessage(requestList.error.value) : ''"
          :is-empty="(requestList.data.value ?? []).length === 0"
          empty-text="Aucune demande."
          @retry="requestList.refresh()"
        >
          <ul class="space-y-2">
            <li
              v-for="(request, index) in requestList.data.value"
              :key="index"
              :class="CARD_CLASSES"
            >
              <p class="font-semibold">
                {{ describeCriteria(request.criteria) }}
              </p>
              <p class="text-sm text-fg-muted">
                Envoyée le {{ formatShortDate(request.createdOn) }}
              </p>
            </li>
          </ul>
        </UiAsyncState>
      </section>
    </div>
  </div>
</template>
