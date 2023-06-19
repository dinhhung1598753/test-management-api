<script lang="ts" setup>
const pageContainer = ref<HTMLElement>();

const nuxtApp = useNuxtApp();
nuxtApp.hook("page:finish", () => {
  pageContainer.value?.scroll(0, 0);
});

definePageMeta({
  middleware: ['auth'],
});

</script>
<template>
  <div class="app-layout">
    <!-- <layout-header /> -->
    <layout-navigation />
    <div ref="pageContainer" class="page-container">
      <slot />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.app-layout {
  height: 100vh;
  overflow: hidden;

  > .page-container {
    font-size: $font-sm;
    padding: 16px;
    overflow-y: scroll;
    height: calc(100% - #{$header-height});
    margin-left: 254px;
  }
}
</style>
