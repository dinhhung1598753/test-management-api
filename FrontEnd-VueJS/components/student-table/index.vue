<script lang="ts" setup>
import { Student } from "@/types";
import { useStudentStore } from "@/stores/student";

const isEditStudent = ref(false);
const studentStore = useStudentStore();

const studentById = ref({});
const titleSnack = ref("");
const isShowSnack = ref(false);
//get students
const res = await studentStore.getStudents();
const students = computed(() => studentStore.students);

const openDialogEditStudent = (student: object) => {
  studentById.value = student;
  isEditStudent.value = true;
};

const closeDialog = () => {
  isEditStudent.value = false;
};

const editStudent = async (e: any) => {
  console.log("value", e.student.value);
  const id = e.student.value.id;
  const fullName = e.student.value.fullName;
  const code = e.student.value.code;
  const birthday = e.student.value.birthday;
  const gender = e.student.value.gender;
  const phoneNumber = e.student.value.phoneNumber;
  const course = e.student.value.course;
  const email = e.student.value.email;
  const res = await studentStore.updateById(
    id,
    fullName,
    code,
    birthday,
    gender,
    phoneNumber,
    course,
    email
  );

  isEditStudent.value = false;
};

// delete

const deleteStudent = async (id: number) => {
  const res = await studentStore.deleteById(id);
  isShowSnack.value = true;
  titleSnack.value = "Xoá thành công";
};
</script>

<template>
  <h2 class="title">DANH SÁCH SINH VIÊN</h2>
  <student-table-create />
  <div class="list-students">
    <v-table fixed-header height="500px">
      <thead>
        <tr>
          <th class="text-left">Họ và tên</th>
          <th class="text-left">MSSV</th>
          <th class="text-left">Năm sinh</th>
          <th class="text-left">Giới tính</th>
          <th class="text-left">Điện thoại</th>
          <th class="text-left">Khoá</th>
          <th class="text-left">Email</th>
          <th class="text-left">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="student in students" :key="student.id">
          <td>{{ student.fullName }}</td>
          <td>{{ student.code }}</td>
          <td>{{ student.birthday }}</td>
          <td>{{ student.gender }}</td>
          <td>{{ student.phoneNumber }}</td>
          <td>{{ student.course }}</td>
          <td>{{ student.email }}</td>
          <td class="action">
            <v-icon
              size="small"
              class="me-2"
              @click="openDialogEditStudent(student)"
            >
              mdi-pencil
            </v-icon>
            <v-icon size="small" @click="deleteStudent(student.id)">
              mdi-delete
            </v-icon>
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>

  <student-table-edit
    :isEditStudent="isEditStudent"
    :studentById="studentById"
    @close="closeDialog"
    @edit="editStudent"
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
.list-students {
  margin-top: 16px;
  cursor: pointer;
}

.wrap {
  display: flex;
  align-items: center;
}
</style>
