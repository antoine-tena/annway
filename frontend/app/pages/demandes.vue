<script setup lang="ts">
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAlerts } from '~/composables/useAlerts'
import { describeCriteria } from '~/utils/criteria'
import { formatShortDate } from '~/utils/format'
import { buttonClasses, CARD_CLASSES } from '~/utils/ui'

definePageMeta({ middleware: 'auth', role: 'DRIVER' })

const alerts = useAlerts()
const { data, status, error, refresh } = await useAsyncData('trip-requests', () => alerts.allRequests())
</script>

<template>
  <div class="mx-auto max-w-4xl px-(--gutter-page) py-12">
    <UiPageHeader
      kicker="Conducteur"
      title="Demandes de trajet"
      subtitle="Les trajets que des passagers cherchent sans les trouver. Publiez-en un qui leur convient : leurs alertes les préviendront."
    />
    <UiAsyncState
      :is-loading="status === 'pending' && !data"
      :error="error ? errorMessage(error) : ''"
      :is-empty="(data ?? []).length === 0"
      empty-text="Aucune demande en attente."
      @retry="refresh()"
    >
      <ul class="space-y-3">
        <li
          v-for="(request, index) in data"
          :key="index"
          :class="[CARD_CLASSES, 'flex flex-wrap items-center justify-between gap-4']"
        >
          <div>
            <p class="font-semibold">
              {{ describeCriteria(request.criteria) }}
            </p>
            <p class="text-sm text-fg-muted">
              {{ request.passenger.firstName }} · le {{ formatShortDate(request.createdOn) }}
            </p>
          </div>
          <NuxtLink
            to="/trajets/publier"
            :class="buttonClasses('secondary', 'sm')"
          >
            Proposer ce trajet
          </NuxtLink>
        </li>
      </ul>
    </UiAsyncState>
  </div>
</template>
