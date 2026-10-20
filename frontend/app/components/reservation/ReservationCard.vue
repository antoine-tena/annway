<script setup lang="ts">
import { computed, ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiStatusPill from '~/components/ui/UiStatusPill.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useReservations } from '~/composables/useReservations'
import type { Reservation, Role } from '~/types/api'
import {
  EQUIPMENT_LABELS,
  FREE_CANCELLATION_NOTICE_HOURS,
  RESERVATION_STATUS_LABELS,
  RESERVATION_STATUS_TONES,
} from '~/utils/constants'
import { eurosToCents, formatCents, formatDay, formatTime } from '~/utils/format'
import { CARD_CLASSES } from '~/utils/ui'

/**
 * Une réservation, vue par son passager (payer, annuler) ou par le conducteur du trajet
 * (valider, refuser). Sur une navette Polytech, le paiement est un don libre. Une réservation
 * payée est remboursée si le conducteur la refuse ou annule le trajet, ou si le passager annule
 * assez tôt.
 */
const { reservation, role } = defineProps<{ reservation: Reservation, role: Role }>()
const emit = defineEmits<{ changed: [reservation: Reservation] }>()

const reservations = useReservations()
const donation = ref('')
const pending = ref('')
const error = ref('')

const isOpen = computed(() => reservation.status === 'PENDING' || reservation.status === 'VALIDATED')
const canPay = computed(() => role === 'PASSENGER' && isOpen.value && !reservation.payment)
const donationCents = computed(() => eurosToCents(donation.value))
const equipmentText = computed(() => reservation.equipment
  .map(article => `${EQUIPMENT_LABELS[article.type]} (${article.description})`)
  .join(', '))

async function run(action: string, call: () => Promise<Reservation>): Promise<void> {
  pending.value = action
  error.value = ''
  try {
    emit('changed', await call())
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    pending.value = ''
  }
}

function pay(): void {
  const amount = reservation.trip.isPaymentRequired ? reservation.amountDueCents : donationCents.value
  if (!amount) {
    error.value = 'Indiquez un montant en euros, par exemple 3.'
    return
  }
  run('pay', () => reservations.pay(reservation.id, amount))
}
</script>

<template>
  <article :class="[CARD_CLASSES, 'space-y-4']">
    <div class="flex flex-wrap items-start justify-between gap-3">
      <div>
        <NuxtLink
          :to="`/trajets/${reservation.trip.id}`"
          class="text-lg font-semibold hover:text-accent-fg"
        >
          {{ reservation.trip.departure }} → {{ reservation.trip.arrival }}
        </NuxtLink>
        <p class="text-sm capitalize text-fg-muted">
          {{ formatDay(reservation.trip.departureAt) }} · {{ formatTime(reservation.trip.departureAt) }}
        </p>
      </div>
      <UiStatusPill
        :label="RESERVATION_STATUS_LABELS[reservation.status]"
        :tone="RESERVATION_STATUS_TONES[reservation.status]"
      />
    </div>
    <p class="text-sm">
      <template v-if="role === 'DRIVER'">
        <NuxtLink
          :to="`/membres/${reservation.passenger.id}`"
          class="font-semibold hover:text-accent-fg"
        >{{ reservation.passenger.firstName }}</NuxtLink> ·
      </template>
      {{ reservation.seatCount }} place{{ reservation.seatCount > 1 ? 's' : '' }}
      <template v-if="reservation.equipment.length">
        · {{ equipmentText }}
      </template>
    </p>
    <p class="text-sm text-fg-muted">
      <template v-if="reservation.payment">
        Payé {{ formatCents(reservation.payment.amountCents) }}
        {{ reservation.payment.type === 'DONATION' ? '(don)' : '' }}
        <template v-if="reservation.refund">
          · remboursé {{ formatCents(reservation.refund.amountCents) }}
        </template>
        <template v-else-if="reservation.refundDueCents">
          · remboursement de {{ formatCents(reservation.refundDueCents) }} en attente
        </template>
        <template v-else-if="isOpen">
          · remboursé si annulé au moins {{ FREE_CANCELLATION_NOTICE_HOURS }} h avant le départ
        </template>
      </template>
      <template v-else-if="reservation.trip.isPaymentRequired">
        À payer : {{ formatCents(reservation.amountDueCents) }}, avant la validation du conducteur.
      </template>
      <template v-else>
        Navette Polytech : don libre.
      </template>
    </p>
    <UiMessage :message="error" />

    <div
      v-if="role === 'PASSENGER' && isOpen"
      class="flex flex-wrap items-end gap-3"
    >
      <UiInput
        v-if="canPay && !reservation.trip.isPaymentRequired"
        v-model="donation"
        label="Don (€)"
        inputmode="decimal"
        placeholder="3"
      />
      <UiButton
        v-if="canPay"
        :loading="pending === 'pay'"
        @click="pay"
      >
        {{ reservation.trip.isPaymentRequired ? `Payer ${formatCents(reservation.amountDueCents)}` : 'Faire un don' }}
      </UiButton>
      <UiButton
        variant="danger"
        :loading="pending === 'cancel'"
        @click="run('cancel', () => reservations.cancel(reservation.id))"
      >
        Annuler
      </UiButton>
    </div>
    <div
      v-if="role === 'DRIVER' && reservation.status === 'PENDING'"
      class="flex flex-wrap gap-3"
    >
      <UiButton
        :loading="pending === 'validate'"
        @click="run('validate', () => reservations.validate(reservation.id))"
      >
        Valider
      </UiButton>
      <UiButton
        variant="danger"
        :loading="pending === 'refuse'"
        @click="run('refuse', () => reservations.refuse(reservation.id))"
      >
        Refuser
      </UiButton>
    </div>
  </article>
</template>
