<script lang="ts" setup>
import { UserInfo } from "@/types";
import { getStudents } from "@/models/student";

const students = ref<UserInfo[]>();

const res = await getStudents();
console.log(123, res);
students.value = res?.data || [];

// const students: UserInfo[] = [
//   {
//     id: 1,
//     fullName: "Nguyễn Thị Hồng Hạnh",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     joinDate: "02/05/2023",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 2,
//     fullName: "Nguyễn Thị Hồng Hạnh",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     joinDate: "02/05/2023",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 3,
//     fullName: "Nguyễn Thị Hồng Hạnh",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     joinDate: "02/05/2023",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 4,
//     fullName: "Nguyễn Thị Hồng Hạnh",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     joinDate: "02/05/2023",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
//   {
//     id: 5,
//     fullName: "Nguyễn Thị Hồng Hạnh",
//     birthday: "22/10/2000",
//     gender: "Nữ",
//     joinDate: "02/05/2023",
//     phoneNumber: "0975256563",
//     email: "nguyenhonghanh@gmail.com",
//   },
// ];

const isCreateStudent = ref(false);
const isDeleteStudent = ref(false);

const createStudent = () => {
  isCreateStudent.value = true;
};

const deleteStudent = () => {
  isDeleteStudent.value = true;
};
</script>

<template>
  <h2 class="title">Thông tin sinh viên</h2>
  <div class="student-info">
    <div class="action">
      <v-btn @click="createStudent"
        ><v-icon icon="mdi-plus" />Thêm Sinh viên</v-btn
      >
      <v-file-input
        clearable
        label="Import danh sách sinh viên"
        variant="underlined"
      ></v-file-input>
      <search />
    </div>

    <div class="list-students">
      <v-table>
        <thead>
          <tr>
            <th class="text-left">Họ và tên</th>
            <th class="text-left">MSSV</th>
            <th class="text-left">Năm sinh</th>
            <th class="text-left">Giới tính</th>
            <th class="text-left">Ngày đăng kí</th>
            <th class="text-left">Điện thoại</th>
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
            <td>{{ student.joinDate }}</td>
            <td>{{ student.phoneNumber }}</td>
            <td>{{ student.email }}</td>
            <td class="action">
              <v-icon size="small" class="me-2" @click="createStudent">
                mdi-pencil
              </v-icon>
              <v-icon size="small" @click="deleteStudent"> mdi-delete </v-icon>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div class="update-student">
      <v-row justify="center">
        <v-dialog v-model="isCreateStudent" persistent width="800">
          <v-card>
            <v-card-title>
              <span class="text-h5">Thêm mới sinh viên</span>
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
                      :placeholder="'Ngày đăng ký'"
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
                @click="isCreateStudent = false"
              >
                Huỷ
              </v-btn>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isCreateStudent = false"
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
        <v-dialog v-model="isDeleteStudent" persistent width="500">
          <v-card>
            <v-card-text>
              <h3>Bạn có chắc chắn muốn xoá sinh viên này không?</h3>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isDeleteStudent = false"
              >
                Huỷ
              </v-btn>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isDeleteStudent = false"
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
