<script lang="ts" setup>
import { MenuItem, AUTH_USER } from "@/types";

const authUser = ref("teacher"); // TODO: check auth user to show navigation
const menuByAdmin: MenuItem[] = [
  {
    title: "Trang chủ",
    icon: "mdi-home-city",
    path: "/",
  },
  {
    title: "Quản lí học sinh",
    icon: "mdi-account-group-outline",
    path: "/admin/students",
  },
  {
    title: "Quản lí giáo viên",
    icon: "mdi-account-group-outline",
    path: "/admin/teachers",
  },
  {
    title: "Quản lí ngân hàng câu hỏi",
    icon: "mdi-book-open-page-variant",
    path: "/admin/questions-management",
  },
  {
    title: "Quản lí tài khoản",
    icon: "mdi-account-circle",
    path: "/admin/profile",
  },
];

const menuByStudent: MenuItem[] = [
  {
    title: "Trang chủ",
    icon: "mdi-home-city",
    path: "/student/home",
  },
  {
    title: "Thư viện đề thi",
    icon: "mdi-book-open",
    path: "/student/tests",
  },
  {
    title: "Thi online",
    icon: "mdi-lead-pencil",
    path: "/student/online-exam",
  },
  {
    title: "Quản lí tài khoản",
    icon: "mdi-account-circle",
    path: "/student/profile",
  },
];

const menuByTeacher: MenuItem[] = [
  {
    title: "Trang chủ",
    icon: "mdi-home-city",
    path: "/",
  },
  {
    title: "Quản lí học sinh",
    icon: "mdi-account-group-outline",
    path: "/teacher/students",
  },
  {
    title: "Quản lí câu hỏi",
    icon: "mdi-comment-question-outline",
    path: "/teacher/questions-management",
  },
  {
    title: "Quản lí môn học",
    icon: "mdi-book-open-page-variant",
    path: "/teacher/subjects",
  },

  {
    title: "Tạo lớp thi",
    icon: "mdi-calendar-clock",
    path: "/teacher/create-class",
  },
  {
    title: "Chấm bài thi offline",
    icon: "mdi-lead-pencil",
    path: "/teacher/mark-the-exam",
  },
  {
    title: "Quản lí tài khoản",
    icon: "mdi-account-circle",
    path: "/teacher/profile",
  },
];

const menus = computed(() => {
  if (authUser.value === AUTH_USER.admin) return menuByAdmin;
  else if (authUser.value === AUTH_USER.student) return menuByStudent;
  return menuByTeacher;
});

const router = useRouter();
const route = useRoute();

const activeMenu = computed(() => {
  return route.path;
});

const handleRedirect = (path: string) => {
  if (activeMenu.value !== path) {
    router.push(path);
  }
};
</script>

<template>
  <v-card>
    <v-layout>
      <v-navigation-drawer permanent location="left">
        <template v-slot:prepend>
          <v-list-item
            lines="two"
            prepend-avatar="https://randomuser.me/api/portraits/women/81.jpg"
            subtitle="Logged in"
          ></v-list-item>
        </template>

        <v-divider></v-divider>

        <v-list density="compact" nav v-for="item in menus" :key="item.title">
          <v-list-item
            @click.stop="handleRedirect(item.path)"
            :prepend-icon="item.icon"
            :title="item.title"
            value="home"
          ></v-list-item>
        </v-list>
      </v-navigation-drawer>
    </v-layout>
  </v-card>
</template>
