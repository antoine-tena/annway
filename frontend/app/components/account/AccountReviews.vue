<script setup lang="ts">
import { ref } from 'vue'
import UiButton from '~/components/ui/UiButton.vue'
import UiInput from '~/components/ui/UiInput.vue'
import UiMessage from '~/components/ui/UiMessage.vue'
import { errorMessage } from '~/composables/useApiClient'
import { useAccount } from '~/composables/useAccount'
import type { Me, Review } from '~/types/api'
import { formatShortDate } from '~/utils/format'

/** Avis reçus ; sur son propre compte (`canReply`), on répond une fois à chacun. */
const { reviews, canReply = false } = defineProps<{ reviews: Review[], canReply?: boolean }>()
const emit = defineEmits<{ replied: [me: Me] }>()

const account = useAccount()
const drafts = ref<Record<number, string>>({})
const error = ref('')

async function reply(review: Review): Promise<void> {
  error.value = ''
  try {
    emit('replied', await account.reply(review.index, drafts.value[review.index] ?? ''))
  } catch (caught: unknown) {
    error.value = errorMessage(caught)
  }
}
</script>

<template>
  <div class="space-y-3">
    <p
      v-if="reviews.length === 0"
      class="text-sm text-fg-muted"
    >
      Aucun avis pour l'instant.
    </p>
    <article
      v-for="review in reviews"
      :key="review.index"
      class="space-y-2 rounded-control bg-fg/5 p-4"
    >
      <p class="text-sm">
        <span
          class="text-warning"
          :aria-label="`${review.rating} sur 5`"
        >{{ '★'.repeat(review.rating) }}</span>
        <strong class="ml-2">{{ review.author.firstName }}</strong>
        <span class="text-fg-muted"> · {{ formatShortDate(review.date) }}</span>
      </p>
      <p
        v-if="review.comment"
        class="text-sm"
      >
        {{ review.comment }}
      </p>
      <p
        v-if="review.reply"
        class="border-l-2 border-accent-fg pl-3 text-sm text-fg-muted"
      >
        {{ review.reply }}
      </p>
      <form
        v-else-if="canReply"
        class="flex items-end gap-2"
        @submit.prevent="reply(review)"
      >
        <UiInput
          v-model="drafts[review.index]"
          class="flex-1"
          label="Répondre"
          required
        />
        <UiButton
          type="submit"
          size="sm"
          variant="secondary"
        >
          Envoyer
        </UiButton>
      </form>
    </article>
    <UiMessage :message="error" />
  </div>
</template>
