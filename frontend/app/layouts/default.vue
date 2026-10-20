<script setup lang="ts">
import MainFooter from '~/components/footer/MainFooter.vue'
import MainHeader from '~/components/header/MainHeader.vue'
</script>

<template>
  <div class="flex min-h-dvh flex-col bg-surface font-sans text-fg">
    <a
      href="#contenu"
      class="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-(--z-top) focus:rounded-control focus:bg-fg focus:px-4 focus:py-2 focus:text-surface"
    >Aller au contenu</a>
    <MainHeader />
    <!-- L'en-tête est fixe, donc hors flux : le contenu démarre sous lui. -->
    <main
      id="contenu"
      class="flex-1 pt-(--header-height)"
    >
      <slot />
    </main>
    <MainFooter />
  </div>
</template>
