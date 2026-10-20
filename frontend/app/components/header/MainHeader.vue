<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import HeaderLogo from '~/components/header/HeaderLogo.vue'
import { useAuth } from '~/composables/useAuth'
import { errorMessage } from '~/composables/useApiClient'
import { navigationLinks } from '~/utils/navigation'
import { buttonClasses } from '~/utils/ui'

/**
 * En-tête noir d'annski, fixé en haut : logo, liens selon le rôle, compte. Sous le point de
 * rupture `md`, les liens passent dans un panneau ouvert par le bouton de menu.
 */
const { me, logout } = useAuth()
const route = useRoute()
const isMenuOpen = ref(false)
const logoutError = ref('')

const links = computed(() => navigationLinks(me.value?.role ?? null))

watch(() => route.path, () => {
  isMenuOpen.value = false
})

async function onLogout(): Promise<void> {
  try {
    await logout()
    await navigateTo('/')
  } catch (error: unknown) {
    logoutError.value = errorMessage(error)
  }
}

const LINK = 'rounded-control px-3 py-2 text-sm font-semibold text-fg-muted transition-colors hover:text-fg focus:outline-none focus-visible:outline-2 focus-visible:outline-accent-fg'
const ACTIVE_LINK = 'text-fg'
</script>

<template>
  <div data-theme="dark">
    <header class="fixed inset-x-0 top-0 z-(--z-header) border-b border-line bg-surface-sunken/95 backdrop-blur-md">
      <div class="mx-auto flex h-(--header-height) max-w-page items-center justify-between gap-4 px-(--gutter-page)">
        <HeaderLogo />

        <nav
          aria-label="Navigation principale"
          class="hidden items-center gap-1 md:flex"
        >
          <NuxtLink
            v-for="link in links"
            :key="link.to"
            :to="link.to"
            :class="LINK"
            :active-class="ACTIVE_LINK"
          >
            {{ link.label }}
          </NuxtLink>
        </nav>

        <div class="flex items-center gap-2">
          <template v-if="me">
            <NuxtLink
              to="/compte"
              :class="buttonClasses('secondary', 'sm')"
            >
              {{ me.firstName }}
            </NuxtLink>
            <button
              type="button"
              :class="[buttonClasses('ghost', 'sm'), 'hidden sm:inline-flex']"
              @click="onLogout"
            >
              Déconnexion
            </button>
          </template>
          <NuxtLink
            v-else
            to="/connexion"
            :class="buttonClasses('primary', 'sm')"
          >
            Connexion
          </NuxtLink>
          <button
            type="button"
            class="inline-flex h-10 w-10 items-center justify-center rounded-control text-fg hover:bg-fg/10 focus:outline-none focus-visible:outline-2 focus-visible:outline-accent-fg md:hidden"
            :aria-label="isMenuOpen ? 'Fermer le menu' : 'Ouvrir le menu'"
            :aria-expanded="isMenuOpen"
            aria-controls="header-menu"
            @click="isMenuOpen = !isMenuOpen"
          >
            <svg
              class="h-6 w-6"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              aria-hidden="true"
            >
              <path
                v-if="isMenuOpen"
                d="M6 6l12 12M18 6L6 18"
              />
              <path
                v-else
                d="M4 7h16M4 12h16M4 17h16"
              />
            </svg>
          </button>
        </div>
      </div>

      <nav
        v-if="isMenuOpen"
        id="header-menu"
        aria-label="Navigation principale"
        class="border-t border-line bg-surface-sunken px-(--gutter-page) py-3 md:hidden"
      >
        <NuxtLink
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          :class="[LINK, 'block']"
          :active-class="ACTIVE_LINK"
        >
          {{ link.label }}
        </NuxtLink>
        <button
          v-if="me"
          type="button"
          :class="[LINK, 'block w-full text-left']"
          @click="onLogout"
        >
          Déconnexion
        </button>
      </nav>
      <p
        v-if="logoutError"
        role="alert"
        class="bg-danger-soft px-(--gutter-page) py-2 text-sm text-danger"
      >
        {{ logoutError }}
      </p>
    </header>
  </div>
</template>
