<script setup lang="ts">
import { computed, ref } from 'vue'
import AccountReviews from '~/components/account/AccountReviews.vue'
import UiAsyncState from '~/components/ui/UiAsyncState.vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import UiPageHeader from '~/components/ui/UiPageHeader.vue'
import UiSelect from '~/components/ui/UiSelect.vue'
import UiTextarea from '~/components/ui/UiTextarea.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAccount } from '~/composables/useAccount'
import { useAuth } from '~/composables/useAuth'
import { MAX_RATING, MIN_RATING, ROLE_LABELS } from '~/utils/constants'
import { CARD_CLASSES } from '~/utils/ui'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const memberId = Number(route.params.id)
const { me } = useAuth()
const account = useAccount()
const { data: profile, status, error, refresh } = await useAsyncData(`member-${memberId}`, () => account.profile(memberId))

const rating = ref(String(MAX_RATING))
const comment = ref('')
const isSending = ref(false)
const reviewError = ref('')
const ratingOptions = Array.from({ length: MAX_RATING - MIN_RATING + 1 }, (_, offset) => {
  const value = MAX_RATING - offset
  return { value: String(value), label: `${'★'.repeat(value)} (${value}/5)` }
})
const isSelf = computed(() => me.value?.id === memberId)

async function review(): Promise<void> {
  isSending.value = true
  reviewError.value = ''
  try {
    profile.value = await account.review(memberId, Number(rating.value), comment.value)
    comment.value = ''
  } catch (caught: unknown) {
    reviewError.value = errorMessage(caught)
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <div class="mx-auto max-w-4xl px-(--gutter-page) py-12">
    <UiAsyncState
      :is-loading="status === 'pending' && !profile"
      :error="error ? errorMessage(error) : ''"
      :is-empty="!profile"
      empty-text="Ce membre n'existe pas."
      @retry="refresh()"
    >
      <div
        v-if="profile"
        class="space-y-6"
      >
        <UiPageHeader
          :kicker="ROLE_LABELS[profile.role]"
          :title="profile.firstName"
          :subtitle="profile.bio"
        >
          <p class="text-sm text-fg-muted">
            <span
              v-if="profile.isVerified"
              class="text-success"
            >✓ Profil vérifié · </span>
            <template v-if="profile.averageRating !== null">
              ★ {{ profile.averageRating }} ({{ profile.reviewCount }} avis)
            </template>
            <template v-else>
              Pas encore d'avis
            </template>
          </p>
        </UiPageHeader>
        <section
          v-if="profile.vehicles.length"
          :class="CARD_CLASSES"
        >
          <h2 class="mb-3 font-display text-xl font-bold uppercase">
            Véhicules
          </h2>
          <p
            v-for="vehicle in profile.vehicles"
            :key="vehicle.registration"
            class="text-sm"
          >
            {{ vehicle.model }} · {{ vehicle.seatCount }} places
          </p>
        </section>
        <section :class="[CARD_CLASSES, 'space-y-4']">
          <h2 class="font-display text-xl font-bold uppercase">
            Avis
          </h2>
          <AccountReviews :reviews="profile.reviews" />
          <form
            v-if="!isSelf"
            class="space-y-3 border-t border-line pt-4"
            @submit.prevent="review"
          >
            <UiSelect
              v-model="rating"
              label="Note"
              :options="ratingOptions"
            />
            <UiTextarea
              v-model="comment"
              label="Commentaire"
            />
            <UiMessage :message="reviewError" />
            <UiButton
              type="submit"
              variant="secondary"
              :loading="isSending"
            >
              Laisser un avis
            </UiButton>
          </form>
        </section>
      </div>
    </UiAsyncState>
  </div>
</template>
