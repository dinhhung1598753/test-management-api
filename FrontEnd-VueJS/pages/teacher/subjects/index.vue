<script lang="ts" setup>
import { Subject } from "@/types";
import { useSubjectStore } from "@/stores/subject";
const subjectStore = useSubjectStore();

const title = ref("");
const code = ref("");
const description = ref("");
const credit = ref(0);
const titleSnack = ref("");
const isShowSnack = ref(false);
const isShowCreateSubjectForm = ref(false);
const isDisabledCreateButton = ref(false);

const showCreateSubjectForm = () => {
  isShowCreateSubjectForm.value = true;
  isDisabledCreateButton.value = true;
};

const closeForm = () => {
  title.value = "";
  code.value = "";
  description.value = "";
  credit.value = 0;
  isShowCreateSubjectForm.value = false;
  isDisabledCreateButton.value = false;
};
// get
const res = await subjectStore.getSubjects();
const subjects = computed(() => subjectStore.subjects);

// create
const createSubject = async () => {
  const res = await subjectStore.create(
    title.value,
    code.value,
    description.value,
    credit.value
  );
  await subjectStore.getSubjects();
  isShowSnack.value = true;
  titleSnack.value = "Thêm thành công";
  closeForm();
};

//edit
const editSubject = async (
  id: number,
  title: string,
  code: string,
  description: string,
  credit: number
) => {
  const res = await subjectStore.updateById(
    id,
    title,
    code,
    description,
    credit
  );

  isShowSnack.value = true;
  titleSnack.value = "Sửa thành công";
};

// delete

const deleteSubject = async (id: number) => {
  const res = await subjectStore.deleteById(id);
  isShowSnack.value = true;
  titleSnack.value = "Xoá thành công";
};
</script>
<template>
  <h2 class="title">Quản lý môn học</h2>

  <div class="create-subject">
    <v-btn @click="showCreateSubjectForm" :disabled="isDisabledCreateButton"
      >Thêm môn học</v-btn
    >

    <div class="form" v-if="isShowCreateSubjectForm">
      <v-text-field
        v-model="title"
        label="Nhập tên môn"
        required
      ></v-text-field>
      <v-text-field v-model="code" label="Nhập mã môn" required></v-text-field>
      <v-text-field
        v-model="description"
        label="Nhập mô tả"
        required
      ></v-text-field>
      <v-text-field
        v-model="credit"
        label="Nhập số tín chỉ"
        required
      ></v-text-field>
      <v-btn @click="createSubject">Thêm</v-btn>
      <v-btn @click="closeForm">Đóng</v-btn>
    </div>
  </div>

  <div class="search"></div>
  <v-table class="subject-table" fixed-header height="500px">
    <thead>
      <tr class="row-head">
        <th class="cell text-center">STT</th>
        <th class="cell text-center">Tên môn học</th>
        <th class="cell text-center">Mã môn</th>
        <th class="cell text-center">Mô tả</th>
        <th class="cell text-center">Số tín chỉ</th>
        <th class="cell text-center"></th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="subject in subjects" :key="subject.id">
        <td class="text-center">{{ subject.id }}</td>
        <td class="text-center" width="400px">
          <v-text-field v-model="subject.title"></v-text-field>
        </td>
        <td class="text-center" width="150px">
          <v-text-field v-model="subject.code"></v-text-field>
        </td>

        <td class="text-center" width="300px">
          <v-text-field v-model="subject.description"></v-text-field>
        </td>
        <td class="text-center" width="100px">
          <v-text-field v-model="subject.credit"></v-text-field>
        </td>
        <td class="action text-center">
          <v-icon
            size="small"
            class="me-2"
            @click="
              editSubject(
                subject.id,
                subject.title,
                subject.code,
                subject.description,
                subject.credit
              )
            "
          >
            mdi-pencil
          </v-icon>
          <v-icon size="small" @click="deleteSubject(subject.id)">
            mdi-delete
          </v-icon>
        </td>
      </tr>
    </tbody>
  </v-table>

  <template>
    <div class="text-center ma-2">
      <v-snackbar v-model="isShowSnack" :timeout="1200" :color="'#2196F3'">
        {{ titleSnack }}
      </v-snackbar>
    </div>
  </template>
</template>

<style lang="scss" scoped>
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.subject-table {
  cursor: pointer;
}

.form {
  display: flex;
  margin: 32px 0;
  gap: 16px;
}
</style>
