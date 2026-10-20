<script setup lang="ts">
import { computed, ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiSelect from '~/components/ui/UiSelect.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useReservations } from '~/composables/useReservations'
import type { EquipmentType, TripDetail } from '~/types/api'
import { EQUIPMENT_LABELS } from '~/utils/constants'
import { formatCents } from '~/utils/format'
import { CARD_CLASSES } from '~/utils/ui'

/**
 * Demande de places du passager, avec le matériel déclaré (un article par ligne). Le
 * conducteur valide ensuite ; sur un trajet payant, il faut avoir payé avant.
 */
const { trip } = defineProps<{ trip: TripDetail }>()
const emit = defineEmits<{ booked: [] }>()

interface EquipmentLine {
  type: EquipmentType
  description: string
}

const reservations = useReservations()
const seatCount = ref(1)
const equipment = ref<EquipmentLine[]>([])
const isSending = ref(false)
const error = ref('')
const success = ref('')

const equipmentOptions = (Object.keys(EQUIPMENT_LABELS) as EquipmentType[])
  .map(type => ({ value: type, label: EQUIPMENT_LABELS[type] }))
const totalCents = computed(() => trip.pricePerSeatCents * Math.max(seatCount.value, 1))

function addEquipment(): void {
  equipment.value.push({ type: 'SKI', description: '' })
}

async function book(): Promise<void> {
  isSending.value = true
  error.value = ''
  try {
    const lines = equipment.value.map(line => `${line.type}:${line.description.trim() || EQUIPMENT_LABELS[line.type]}`)
    await reservations.book(trip.id, seatCount.value, lines)
    success.value = 'Demande envoyée : le conducteur va répondre. Suivez-la dans « Mes réservations ».'
    equipment.value = []
    emit('booked')
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <form
    :class="[CARD_CLASSES, 'space-y-4']"
    @submit.prevent="book"
  >
    <h2 class="font-display text-xl font-bold uppercase">
      Réserver
    </h2>
    <UiInput
      v-model.number="seatCount"
      label="Places"
      type="number"
      min="1"
      :max="trip.availableSeats"
      required
    />
    <fieldset class="space-y-3">
      <legend class="text-label uppercase text-fg-muted">
        Matériel ({{ trip.remainingEquipmentCapacity }} unités de place restantes)
      </legend>
      <div
        v-for="(line, index) in equipment"
        :key="index"
        class="grid grid-cols-[1fr_2fr_auto] items-end gap-2"
      >
        <UiSelect
          v-model="line.type"
          :label="`Article ${index + 1}`"
          :options="equipmentOptions"
        />
        <UiInput
          v-model="line.description"
          label="Description"
          placeholder="Skis de 170 cm"
        />
        <UiButton
          variant="ghost"
          size="sm"
          :aria-label="`Retirer l'article ${index + 1}`"
          @click="equipment.splice(index, 1)"
        >
          ✕
        </UiButton>
      </div>
      <UiButton
        variant="outline"
        size="sm"
        @click="addEquipment"
      >
        Ajouter du matériel
      </UiButton>
    </fieldset>
    <p
      v-if="trip.isPaymentRequired"
      class="text-sm text-fg-muted"
    >
      Total : <strong class="text-fg">{{ formatCents(totalCents) }}</strong>, à payer avant la validation.
    </p>
    <p
      v-else
      class="text-sm text-fg-muted"
    >
      Navette Polytech : un don libre, si vous le souhaitez.
    </p>
    <UiMessage :message="error" />
    <UiMessage
      :message="success"
      tone="success"
    />
    <UiButton
      type="submit"
      :loading="isSending"
    >
      Demander {{ seatCount > 1 ? `${seatCount} places` : 'une place' }}
    </UiButton>
  </form>
</template>
