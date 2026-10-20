<script setup lang="ts">
import UiInput from '~/components/ui/UiInput.vue'
import type { VehicleInput } from '~/utils/vehicle'

/** Champs d'un véhicule : immatriculation, modèle, places passagers, capacité de matériel. */
const vehicle = defineModel<VehicleInput>({ required: true })
</script>

<template>
  <fieldset class="space-y-4 rounded-card border border-line p-4">
    <legend class="px-1 text-label uppercase text-fg-muted">
      Véhicule
    </legend>
    <div class="grid gap-4 sm:grid-cols-2">
      <UiInput
        v-model="vehicle.registration"
        label="Immatriculation"
        placeholder="AA-123-BB"
        required
      />
      <UiInput
        v-model="vehicle.model"
        label="Modèle"
        placeholder="Peugeot 308"
        required
      />
      <UiInput
        v-model.number="vehicle.seatCount"
        label="Places passagers"
        type="number"
        min="1"
        required
      />
      <UiInput
        v-model.number="vehicle.equipmentCapacity"
        label="Capacité de matériel"
        type="number"
        min="0"
        hint="Unités : une paire de skis ou un snowboard compte 1, une luge 2."
        required
      />
    </div>
  </fieldset>
</template>
