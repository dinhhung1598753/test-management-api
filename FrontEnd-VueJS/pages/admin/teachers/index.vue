<script lang="ts" setup>
import { UserInfo } from "@/types";
import { getTeachers } from "@/models/teacher";

const teachers = ref<UserInfo[]>();

const res = await getTeachers();
console.log(123, res);
teachers.value = res?.data || [];
// const teachers: UserInfo[] = [
//   {
//     id: 1,
//     fullName: "Nguyễn Hồng Phúc",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 2,
//     fullName: "Nguyễn Hồng Phúc",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 3,
//     fullName: "Nguyễn Hồng Phúc",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 4,
//     fullName: "Nguyễn Hồng Phúc",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 5,
//     fullName: "Nguyễn Hồng Phúc",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
// ];
const isCreateTeacher = ref(false);
const isDeleteTeacher = ref(false);

const createTeacher = () => {
  isCreateTeacher.value = true;
};

const deleteTeacher = () => {
  isDeleteTeacher.value = true;
};
</script>

<template>
  <h2 class="title">Thông tin giáo viên</h2>
  <div class="teacher-info">
    <div class="action">
      <v-btn @click="createTeacher"
        ><v-icon icon="mdi-plus" />Thêm Giáo viên</v-btn
      >
      <v-file-input
        clearable
        label="Import danh sách giáo viên"
        variant="underlined"
      ></v-file-input>
      <search />
    </div>

    <div class="list-teachers">
      <v-table>
        <thead>
          <tr>
            <th class="text-left">Họ và tên</th>
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
            <td>{{ teacher.birthday }}</td>
            <td>{{ teacher.gender }}</td>
            <td>{{ teacher.phoneNumber }}</td>
            <td>{{ teacher.email }}</td>
            <td class="action">
              <v-icon size="small" class="me-2" @click="createTeacher">
                mdi-pencil
              </v-icon>
              <v-icon size="small" @click="deleteTeacher"> mdi-delete </v-icon>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div class="update-teacher">
      <v-row justify="center">
        <v-dialog v-model="isCreateTeacher" persistent width="800">
          <v-card>
            <v-card-title>
              <span class="text-h5">Thêm mới giáo viên</span>
            </v-card-title>
            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      required
                      :placeholder="'Họ và tên'"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      required
                      :placeholder="'Năm sinh'"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      required
                      :placeholder="'Giới tính'"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      required
                      :placeholder="'Điện thoại'"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      required
                      :placeholder="'Email'"
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
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isCreateTeacher = false"
              >
                Lưu
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-row>
    </div>
    <div class="delete-student">
      <v-row justify="center">
        <v-dialog v-model="isDeleteTeacher" persistent width="500">
          <v-card>
            <v-card-text>
              <h3>Bạn có chắc chắn muốn xoá giáo viên này không?</h3>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isDeleteTeacher = false"
              >
                Huỷ
              </v-btn>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isDeleteTeacher = false"
              >
                Xoá
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-row>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.teacher-info {
  > .action {
    width: 900px;
    display: flex;
    align-items: center;
    gap: 60px;
    overflow: hidden;
  }
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
