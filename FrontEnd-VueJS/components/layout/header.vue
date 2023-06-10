<script lang="ts" setup>
import blankAvatar from "@/assets/images/blank-avatar.png";
</script>

<template>
  <nav class="nav-bar">
    <h1 class="title">Test Management</h1>
    <div class="avatar">
      <img
        class="image"
        :src="blankAvatar"
        alt="avatar"
        width="40"
        height="40"
      />
    </div>
  </nav>
</template>

<style lang="scss" scoped>
.nav-bar {
  height: $header-height;
  background-color: $color-white;
  display: flex;
  align-items: center;

  > .title {
    color: $color-black;
    text-align: left;
    padding-left: 24px;
    font-weight: normal;
    font-size: 24px;
    font-weight: 600;
  }

  > .avatar {
    position: absolute;
    right: 24px;
  }

  > .avatar > .image {
    border-radius: 50%;
  }
}
</style>
