<script setup lang="ts">
import { reactive, ref } from 'vue'
import AccountVehicleFields from '~/components/account/AccountVehicleFields.vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import { ApiError, errorMessage } from '~/composables/useApiClient'
import { useAuth } from '~/composables/useAuth'
import type { Role } from '~/types/api'
import { MIN_PASSWORD_LENGTH, ROLE_LABELS } from '~/utils/constants'
import { emptyVehicle } from '~/utils/vehicle'

/** Inscription d'un passager, ou d'un conducteur avec son premier véhicule. */
const emit = defineEmits<{ done: [] }>()

const { register } = useAuth()
const role = ref<Role>('PASSENGER')
const person = reactive({ firstName: '', lastName: '', email: '', phone: '', password: '' })
const vehicle = ref(emptyVehicle())
const isSending = ref(false)
const error = ref('')
const errorField = ref('')

async function submit(): Promise<void> {
  isSending.value = true
  error.value = ''
  errorField.value = ''
  try {
    await register({ role: role.value, ...person, ...(role.value === 'DRIVER' ? vehicle.value : {}) })
    emit('done')
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
    errorField.value = caught instanceof ApiError ? caught.field ?? '' : ''
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <form
    class="space-y-4"
    @submit.prevent="submit"
  >
    <fieldset>
      <legend class="mb-2 text-label uppercase text-fg-muted">
        Je suis
      </legend>
      <div class="grid grid-cols-2 gap-2 rounded-full bg-fg/5 p-1">
        <label
          v-for="(label, value) in ROLE_LABELS"
          :key="value"
          class="cursor-pointer rounded-full px-4 py-2 text-center text-sm font-semibold transition-colors has-[:checked]:bg-accent has-[:checked]:text-on-accent has-[:focus-visible]:outline-2 has-[:focus-visible]:outline-accent-fg"
        >
          <input
            v-model="role"
            type="radio"
            name="role"
            :value="value"
            class="sr-only"
          >
          {{ label }}
        </label>
      </div>
    </fieldset>
    <div class="grid gap-4 sm:grid-cols-2">
      <UiInput
        v-model="person.firstName"
        label="Prénom"
        autocomplete="given-name"
        required
      />
      <UiInput
        v-model="person.lastName"
        label="Nom"
        autocomplete="family-name"
        required
      />
    </div>
    <UiInput
      v-model="person.email"
      label="E-mail"
      type="email"
      autocomplete="email"
      :error="errorField === 'email' ? error : ''"
      required
    />
    <UiInput
      v-model="person.phone"
      label="Téléphone"
      type="tel"
      autocomplete="tel"
      required
    />
    <UiInput
      v-model="person.password"
      label="Mot de passe"
      type="password"
      autocomplete="new-password"
      :minlength="MIN_PASSWORD_LENGTH"
      :hint="`${MIN_PASSWORD_LENGTH} caractères au moins.`"
      :error="errorField === 'password' ? error : ''"
      required
    />
    <AccountVehicleFields
      v-if="role === 'DRIVER'"
      v-model="vehicle"
    />
    <UiMessage :message="errorField === 'email' || errorField === 'password' ? '' : error" />
    <UiButton
      type="submit"
      class="w-full"
      :loading="isSending"
    >
      Créer mon compte
    </UiButton>
  </form>
</template>
