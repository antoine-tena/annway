<script setup lang="ts">
import { computed } from 'vue'
import AuthLoginForm from '~/components/auth/AuthLoginForm.vue'
import AuthRegisterForm from '~/components/auth/AuthRegisterForm.vue'

/**
 * Connexion et inscription sur une seule page, à la manière du portail d'annski : le formulaire
 * d'un côté, un panneau bleu de l'autre. `?mode=inscription` ouvre l'inscription, `?retour=`
 * ramène à la page demandée.
 */
const route = useRoute()
const router = useRouter()

const isRegisterMode = computed(() => route.query.mode === 'inscription')

function switchMode(mode: 'connexion' | 'inscription'): void {
  router.replace({ query: { ...route.query, mode: mode === 'inscription' ? 'inscription' : undefined } })
}

async function done(): Promise<void> {
  // Chemin interne seulement : `//site.example` mènerait hors d'annway.
  const retour = route.query.retour
  const isInternal = typeof retour === 'string' && retour.startsWith('/') && !retour.startsWith('//')
  const target = isInternal ? retour : '/trajets'
  await navigateTo(target)
}

const TAB = 'flex-1 rounded-full px-4 py-2 text-sm font-semibold transition-colors focus:outline-none focus-visible:outline-2 focus-visible:outline-accent-fg'
</script>

<template>
  <div class="mx-auto max-w-5xl px-(--gutter-page) py-12">
    <div class="grid overflow-hidden rounded-panel border border-line bg-surface-raised md:grid-cols-2">
      <section class="p-6 sm:p-10">
        <div
          class="mb-8 flex gap-2 rounded-full bg-fg/5 p-1.5"
          role="group"
          aria-label="Choisir entre connexion et inscription"
        >
          <button
            type="button"
            :class="[TAB, isRegisterMode ? 'text-fg-muted hover:text-fg' : 'bg-accent text-on-accent']"
            :aria-pressed="!isRegisterMode"
            @click="switchMode('connexion')"
          >
            Connexion
          </button>
          <button
            type="button"
            :class="[TAB, isRegisterMode ? 'bg-accent text-on-accent' : 'text-fg-muted hover:text-fg']"
            :aria-pressed="isRegisterMode"
            @click="switchMode('inscription')"
          >
            Inscription
          </button>
        </div>
        <h1 class="mb-6 font-display text-display-sm font-bold uppercase leading-display">
          {{ isRegisterMode ? 'Créer un compte' : 'Bon retour !' }}
        </h1>
        <AuthRegisterForm
          v-if="isRegisterMode"
          @done="done"
        />
        <AuthLoginForm
          v-else
          @done="done"
        />
      </section>
      <aside class="hidden flex-col justify-center gap-4 bg-linear-155 from-accent-hover to-accent p-10 text-on-accent md:flex md:rounded-l-[140px]">
        <p class="text-kicker uppercase opacity-80">
          annway
        </p>
        <p class="font-display text-display-sm font-bold uppercase leading-display">
          {{ isRegisterMode ? 'Rejoignez le covoiturage des sorties' : 'Vos trajets vous attendent' }}
        </p>
        <p class="opacity-90">
          {{ isRegisterMode
            ? 'Passager, réservez une place et déclarez votre matériel. Conducteur, publiez vos trajets et choisissez vos passagers.'
            : 'Retrouvez vos réservations, vos alertes et les messages de vos trajets.' }}
        </p>
      </aside>
    </div>
  </div>
</template>
