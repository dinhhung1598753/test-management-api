<script lang="ts" setup>
import {signUp } from '@/models/auth'

definePageMeta({
  layout: "auth",
});

const form = ref()
const username = ref('')
const email = ref('')
const password = ref('')

const onSubmit = async ()=>{
  if(!form) return
  const res = await signUp(username.value, email.value, password.value)
}

const requiredName = (v: any) => !!v || `Full name is required`
const requiredEmail = (v: any) => {
//   if(!v ) return true
//   if (/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(v)) return true
//   return 'Must be a valid e-mail.'
 return true
}
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
          label="Nhập họ và tên"
        ></v-text-field>

        <v-text-field
          v-model="email"
          :rules="[requiredEmail]"
          class="mb-2"
          clearable
          label="Nhập email"
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
          Đăng ký
        </v-btn>
      </v-form>
    </v-card>
  </v-sheet>
</template>
