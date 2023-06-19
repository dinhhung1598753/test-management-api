<script lang="ts" setup>
import { UserInfo } from "@/types";
import blankAvatar from "@/assets/images/blank-avatar.png";

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({}),
  },
});
</script>

<template>
  <h2 class="title">Thông tin cá nhân</h2>
  <div class="user-information">
    <div class="avatar">
      <img
        class="image"
        :src="blankAvatar"
        alt="avatar"
        width="200"
        height="200"
      />
      <v-btn variant="outlined" class="button"
        ><v-icon icon="mdi-camera" /> Chọn ảnh mới
      </v-btn>
    </div>

    <div class="wrap">
      <div class="info">
        <div class="detail">
          <v-text-field
            v-model="username"
            :rules="nameRules"
            :counter="10"
            label="Tên đăng nhập"
            required
          ></v-text-field>
          <v-text-field
            v-model="userInfo.fullName"
            :rules="nameRules"
            :counter="10"
            label="Họ và tên"
            required
          ></v-text-field>
          <v-text-field
            v-model="userInfo.gender"
            :rules="nameRules"
            :counter="10"
            label="Giới tính"
            required
          ></v-text-field>
        </div>
        <div class="detail">
          <v-text-field
            v-model="userInfo.birthday"
            :rules="nameRules"
            :counter="10"
            label="Năm sinh"
            required
          ></v-text-field>
          <v-text-field
            v-model="userInfo.phoneNumber"
            :rules="nameRules"
            :counter="10"
            label="Điện thoại"
            required
          ></v-text-field>
          <v-text-field
            v-model="userInfo.email"
            :rules="emailRules"
            :counter="10"
            label="Email"
            required
          ></v-text-field>
        </div>
      </div>
      <v-btn variant="outlined" class="action">Cập nhật </v-btn>
    </div>
  </div>
</template>

<style scoped lang="scss">
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.user-information {
  display: flex;
  margin-top: 32px;
  > .avatar {
    display: flex;
    flex-direction: column;
    width: 200px;
    height: 200px;
    border: 1px solid $color-gray;
  }
  > .avatar > .button {
    margin-top: 12px;
  }

  > .wrap {
    margin-left: 32px;
    width: 40%;
  }
}
</style>
