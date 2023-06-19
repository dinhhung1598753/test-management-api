<script lang="ts" setup>
import { Teacher } from "@/types";
import { useTeacherStore } from "@/stores/teacher";

const isEditTeacher = ref(false);
const teacherStore = useTeacherStore();

const teacherById = ref({});
const titleSnack = ref("");
const isShowSnack = ref(false);
//get teachers
const res = await teacherStore.getTeachers();
const teachers = computed(() => teacherStore.teachers);

const openDialogEditTeacher = (teacher: object) => {
  teacherById.value = teacher;
  isEditTeacher.value = true;
};

const closeDialog = () => {
  isEditTeacher.value = false;
};

const editTeacher = async (e: any) => {
  console.log("value", e.teacher.value);
  const id = e.teacher.value.id;
  const fullName = e.teacher.value.fullName;
  const code = e.teacher.value.code;
  const birthday = e.teacher.value.birthday;
  const gender = e.teacher.value.gender;
  const phoneNumber = e.teacher.value.phoneNumber;
  const email = e.teacher.value.email;
  const res = await teacherStore.updateById(
    id,
    fullName,
    code,
    birthday,
    gender,
    phoneNumber,
    email
  );

  isEditTeacher.value = false;
};

// delete

const deleteTeacher = async (id: number) => {
  const res = await teacherStore.deleteById(id);
  isShowSnack.value = true;
  titleSnack.value = "Xoá thành công";
};
</script>

<template>
  <h2 class="title">DANH SÁCH GIÁO VIÊN</h2>
  <teacher-table-create />
  <div class="list-teachers">
    <v-table fixed-header height="500px">
      <thead>
        <tr>
          <th class="text-left">Họ và tên</th>
          <th class="text-left">Mã GV</th>
          <th class="text-left">Năm sinh</th>
          <th class="text-left">Giới tính</th>
          <th class="text-left">Điện thoại</th>
          <th class="text-left">Email</th>
          <th class="text-left">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="teacher in teachers" :key="teacher.id">
          <td>{{ teacher.fullName }}</td>
          <td>{{ teacher.code }}</td>
          <td>{{ teacher.birthday }}</td>
          <td>{{ teacher.gender }}</td>
          <td>{{ teacher.phoneNumber }}</td>
          <td>{{ teacher.email }}</td>
          <td class="action">
            <v-icon
              size="small"
              class="me-2"
              @click="openDialogEditTeacher(teacher)"
            >
              mdi-pencil
            </v-icon>
            <v-icon size="small" @click="deleteTeacher(teacher.id)">
              mdi-delete
            </v-icon>
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>

  <teacher-table-edit
    :isEditTeacher="isEditTeacher"
    :teacherById="teacherById"
    @close="closeDialog"
    @edit="editTeacher"
  />
  <template>
    <div class="text-center ma-2">
      <v-snackbar v-model="isShowSnack" :timeout="1200" :color="'#2196F3'">
        {{ titleSnack }}
      </v-snackbar>
    </div>
  </template>
</template>

<style scoped lang="scss">
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}
.list-teachers {
  margin-top: 16px;
  cursor: pointer;
}

.wrap {
  display: flex;
  align-items: center;
}
</style>
