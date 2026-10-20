<script setup lang="ts">
import { ref, watch } from 'vue'
import AccountReviews from '~/components/account/AccountReviews.vue'
import AccountVehicleFields from '~/components/account/AccountVehicleFields.vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import UiStatusPill from '~/components/ui/UiStatusPill.vue'
import UiTextarea from '~/components/ui/UiTextarea.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAccount } from '~/composables/useAccount'
import { useAuth } from '~/composables/useAuth'
import type { Me } from '~/types/api'
import { ROLE_LABELS } from '~/utils/constants'
import { CARD_CLASSES } from '~/utils/ui'
import { emptyVehicle } from '~/utils/vehicle'

definePageMeta({ middleware: 'auth' })

const { me } = useAuth()
const account = useAccount()
const bio = ref(me.value?.bio ?? '')
const vehicle = ref(emptyVehicle())
const isAddingVehicle = ref(false)
const pending = ref('')
const error = ref('')
const success = ref('')

watch(() => me.value?.bio, (current) => {
  bio.value = current ?? ''
})

async function save(action: string, call: () => Promise<Me>, confirmation: string): Promise<void> {
  pending.value = action
  error.value = ''
  success.value = ''
  try {
    me.value = await call()
    success.value = confirmation
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  } finally {
    pending.value = ''
  }
}

async function addVehicle(): Promise<void> {
  await save('vehicle', () => account.addVehicle({ ...vehicle.value }), 'Véhicule ajouté.')
  if (!error.value) {
    vehicle.value = emptyVehicle()
    isAddingVehicle.value = false
  }
}

const VERIFICATIONS = [
  { key: 'email', label: 'E-mail' },
  { key: 'phone', label: 'Téléphone' },
  { key: 'identityDocument', label: 'Pièce d\'identité' },
] as const
</script>

<template>
  <div
    v-if="me"
    class="mx-auto max-w-page px-(--gutter-page) py-12"
  >
    <UiPageHeader
      :kicker="ROLE_LABELS[me.role]"
      :title="`${me.firstName} ${me.lastName}`"
    >
      <p class="text-sm text-fg-muted">
        {{ me.email }} · {{ me.phone }}
        <template v-if="me.averageRating !== null">
          · ★ {{ me.averageRating }} ({{ me.reviewCount }} avis)
        </template>
      </p>
    </UiPageHeader>
    <div class="mb-6 space-y-3">
      <UiMessage :message="error" />
      <UiMessage
        :message="success"
        tone="success"
      />
    </div>

    <div class="grid gap-6 lg:grid-cols-2">
      <section :class="[CARD_CLASSES, 'space-y-4']">
        <h2 class="font-display text-xl font-bold uppercase">
          Profil
        </h2>
        <ul class="flex flex-wrap gap-2">
          <li
            v-for="verification in VERIFICATIONS"
            :key="verification.key"
          >
            <UiStatusPill
              :label="`${verification.label} ${me.verifications[verification.key] ? 'vérifié' : 'à vérifier'}`"
              :tone="me.verifications[verification.key] ? 'success' : 'muted'"
            />
          </li>
        </ul>
        <form
          class="space-y-3"
          @submit.prevent="save('bio', () => account.updateBio(bio), 'Présentation enregistrée.')"
        >
          <UiTextarea
            v-model="bio"
            label="Présentation"
            placeholder="Niveau de ski, horaires habituels, matériel…"
          />
          <UiButton
            type="submit"
            variant="secondary"
            :loading="pending === 'bio'"
          >
            Enregistrer
          </UiButton>
        </form>
      </section>

      <section
        v-if="me.role === 'DRIVER'"
        :class="[CARD_CLASSES, 'space-y-4']"
      >
        <h2 class="font-display text-xl font-bold uppercase">
          Véhicules
        </h2>
        <ul class="space-y-2">
          <li
            v-for="owned in me.vehicles"
            :key="owned.registration"
            class="flex justify-between gap-3 rounded-control bg-fg/5 px-4 py-3 text-sm"
          >
            <span><strong>{{ owned.model }}</strong> · {{ owned.registration }}</span>
            <span class="text-fg-muted">{{ owned.seatCount }} places · {{ owned.equipmentCapacity }} unités</span>
          </li>
        </ul>
        <form
          v-if="isAddingVehicle"
          class="space-y-3"
          @submit.prevent="addVehicle"
        >
          <AccountVehicleFields v-model="vehicle" />
          <UiButton
            type="submit"
            :loading="pending === 'vehicle'"
          >
            Ajouter ce véhicule
          </UiButton>
        </form>
        <UiButton
          v-else
          variant="outline"
          @click="isAddingVehicle = true"
        >
          Ajouter un véhicule
        </UiButton>
      </section>

      <section :class="[CARD_CLASSES, 'space-y-4 lg:col-span-2']">
        <h2 class="font-display text-xl font-bold uppercase">
          Avis reçus
        </h2>
        <AccountReviews
          :reviews="me.reviews"
          can-reply
          @replied="updated => (me = updated)"
        />
      </section>
    </div>
  </div>
</template>
