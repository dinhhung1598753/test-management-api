<template>
  <profile :user-info="students" />
</template>
<script lang="ts" setup>
import { UserInfo } from "@/types";
const students: UserInfo = {
  id: 1,
  fullName: "Admin",
  birthday: "22/10/2000",
  gender: "Nữ",
  joinDate: "02/05/2023",
  phoneNumber: "0975256563",
  email: "nguyenhonghanh@gmail.com",
};
</script>
