<script lang="ts" setup>
import {signin } from '@/models/auth'
definePageMeta({
  layout: "auth",
});
const form = ref()
const username = ref('')
const password = ref('')

const onSubmit = async ()=>{
  if(!form) return
  const res = await signin(username.value, password.value)
  console.log(res)
}

const requiredName = (v: any) => !!v || `Full name is required`

const requiredPassword = (v: any) => !!v || `Password is required`

</script>

<template>
  <v-sheet class="bg-deep-purple pa-12" rounded>
    <v-card class="mx-auto px-6 py-8" max-width="344">
      <v-form v-model="form" @submit.prevent="onSubmit">
        <v-text-field
          v-model="username"
          :rules="[requiredName]"
          class="mb-2"
          clearable
          label="Nhập username"
          placeholder="Nhập username"
        ></v-text-field>

        <v-text-field
          v-model="password"
          :rules="[requiredPassword]"
          clearable
          label="Nhập mật khẩu"
          placeholder="Nhập mật khẩu"
        ></v-text-field>

        <br />

        <v-btn
          :disabled="!form"
          block
          color="success"
          size="large"
          type="submit"
          variant="elevated"
        >
          Đăng nhập
        </v-btn>
      </v-form>
    </v-card>
  </v-sheet>
</template>
