<script lang="ts" setup>
const pageContainer = ref<HTMLElement>();

const nuxtApp = useNuxtApp();
nuxtApp.hook("page:finish", () => {
  pageContainer.value?.scroll(0, 0);
});
</script>
<template>
  <div class="app-layout">
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
  }
}
</style>
