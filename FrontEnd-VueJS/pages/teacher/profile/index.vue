<template>
  <profile :user-info="students" />
</template>
<script lang="ts" setup>
import { UserInfo } from "@/types";
const students: UserInfo = {
  id: 1,
  fullName: "Teacher",
  birthday: "2/9/2000",
  gender: "Nam",
  joinDate: "02/05/2023",
  phoneNumber: "0975256563",
  email: "teacher@gmail.com",
};
</script>
