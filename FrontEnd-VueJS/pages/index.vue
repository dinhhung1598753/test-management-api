<script lang="ts">
export default {
  beforeRouteEnter(to, from, next) {
    next({ path: "/admin" });
  },
};
</script>
<template>
  <div class="home-page"><h2 class="title">Welcome to Test Management</h2></div>
</template>
<style lang="scss" scoped>
.home-page {
  padding-top: 100px;
  > .title {
    text-align: center;
  }
}
</style>
