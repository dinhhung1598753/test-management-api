<script lang="ts" setup>
import { Student } from "@/types";
import { useStudentStore } from "@/stores/student";

const studentStore = useStudentStore();

//get students
const res = await studentStore.getStudents();
const students = computed(() => studentStore.students);

// export students
const exportStudents = async () => {
  await studentStore.exportStudents();
};
</script>

<template>
  <h2 class="title">Thông tin sinh viên</h2>
  <div class="student-info">
    <div class="action">
      <v-btn @click="exportStudents">Export Danh sách </v-btn>
      <search />
    </div>

    <div class="list-students">
      <v-table fixed-header height="500px">
        <thead>
          <tr>
            <th class="text-left">Họ và tên</th>
            <th class="text-left">MSSV</th>
            <th class="text-left">Năm sinh</th>
            <th class="text-left">Giới tính</th>
            <th class="text-left">Khóa</th>
            <th class="text-left">Điện thoại</th>
            <th class="text-left">Email</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.id">
            <td>{{ student.fullName }}</td>
            <td>{{ student.code }}</td>
            <td>{{ student.birthday }}</td>
            <td>{{ student.gender }}</td>
            <td>{{ student.course }}</td>
            <td>{{ student.phoneNumber }}</td>
            <td>{{ student.email }}</td>
          </tr>
        </tbody>
      </v-table>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}
.student-info {
  > .action {
    width: 900px;
    display: flex;
    align-items: center;
    gap: 60px;
    overflow: hidden;
  }
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
