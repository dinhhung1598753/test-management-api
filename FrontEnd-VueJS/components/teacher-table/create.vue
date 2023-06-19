<script lang="ts" setup>
import { Teacher } from "@/types";
import { useTeacherStore } from "@/stores/teacher";

const teacherStore = useTeacherStore();

const fullName = ref("");
const code = ref("");
const username = ref("");
const password = ref("");
const birthday = ref("");
const gender = ref("");
const phoneNumber = ref("");
const email = ref("");
const isCreateTeacher = ref(false);

// TODO
const submit = async () => {
  const res = await teacherStore.createTeacher(
    fullName.value,
    code.value,
    username.value,
    password.value,
    birthday.value,
    gender.value,
    phoneNumber.value,
    email.value
  );
  // await teacherStore.getTeachers();
  isCreateTeacher.value = false;
};
const createTeacher = () => {
  isCreateTeacher.value = true;
};
</script>

<template>
  <div class="teacher-management">
    <div class="action">
      <v-btn @click="createTeacher"
        ><v-icon icon="mdi-plus" />Thêm mới sinh viên</v-btn
      >
      <v-file-input
        clearable
        label="Import danh sách giáo viên"
        variant="underlined"
      ></v-file-input>
      <search />
    </div>
    <div class="dialog-create-teacher">
      <v-row justify="center">
        <v-dialog v-model="isCreateTeacher" persistent width="1024">
          <v-card>
            <v-card-title>
              <span class="text-h5">Thêm mới giáo viên</span>
            </v-card-title>
            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="fullName"
                      label="Nhập họ và tên"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="code"
                      label="Nhập mã GV"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="username"
                      label="Nhập tên đăng nhập"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="password"
                      label="Nhập mật khẩu"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="birthday"
                      label="Nhập ngày sinh"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="gender"
                      label="Nhập giới tính"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="phoneNumber"
                      label="Nhập số điện thoại"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="email"
                      label="Nhập email"
                      required
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isCreateTeacher = false"
              >
                Huỷ
              </v-btn>
              <v-btn color="blue-darken-1" variant="text" @click="submit">
                Lưu
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-row>
    </div>
  </div>
</template>
<style scoped lang="scss">
.teacher-management {
  > .action {
    width: 900px;
    display: flex;
    align-items: center;
    gap: 60px;
    overflow: hidden;
  }
}
</style>
