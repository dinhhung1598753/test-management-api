<script lang="ts" setup>
import { Test } from "@/types";
import { useTestStore } from "@/stores/test";
import { useSubjectStore } from "@/stores/subject";

const subjectStore = useSubjectStore();
const testsStore = useTestStore();

const subjectCode = ref("");
const chapterOrders = ref([]);
const questionQuantity = ref(0);
const testDay = ref("");
const duration = ref(0);
const isOpenCreateForm = ref(false);
const titleSnack = ref("");
const isShowSnack = ref(false);

//get subjects, chapters
const result = await subjectStore.getSubjects();
const subjects = computed(() => subjectStore.subjects);

const chapters = computed(() => {
  return subjectStore.chapters;
});
watch(subjectCode, () => {
  subjectStore.getChapters(subjectCode.value);
});

// get tests
const res = await testsStore.getTests();
const tests = computed(() => testsStore.tests);

// create test
const createNewTest = () => {
  isOpenCreateForm.value = true;
};

const submitCreateForm = async () => {
  const res = await testsStore.createTest(
    subjectCode.value,
    chapterOrders.value,
    +questionQuantity.value,
    testDay.value,
    +duration.value
  );
  await testsStore.getTests();
  isShowSnack.value = true;
  titleSnack.value = "Thêm bài test thành công";
  isOpenCreateForm.value = false;
};

const cancelCreateForm = () => {
  subjectCode.value = "";
  chapterOrders.value = [];
  questionQuantity.value = 0;
  testDay.value = "";
  duration.value = 0;
  isOpenCreateForm.value = false;
};

const getTestDetailRoutePath = async (testId: number) => {
  await testsStore.getTestDetail(testId);
};
</script>

<template>
  <h2 class="title">Quản lý bài thi</h2>

  <div class="create-tests">
    <v-btn @click="createNewTest" :disabled="isOpenCreateForm"
      ><v-icon icon="mdi-plus" />Thêm mới bài thi</v-btn
    >

    <div v-if="isOpenCreateForm" class="form-create">
      <div class="form">
        <v-autocomplete
          clearable
          label="Nhập tên môn"
          :items="subjects"
          item-title="title"
          item-value="code"
          v-model="subjectCode"
        ></v-autocomplete>
        <v-select
          label="Chương"
          :items="chapters"
          item-title="order"
          item-value="id"
          class="select"
          multiple
          v-model="chapterOrders"
          :variant="'outlined'"
        ></v-select>
      </div>
      <div class="form">
        <v-text-field
          required
          :placeholder="'Nhập số lượng câu'"
          v-model="questionQuantity"
        ></v-text-field>
        <v-text-field
          required
          :placeholder="'Nhập ngày kiểm tra'"
          v-model="testDay"
        ></v-text-field>
        <v-text-field
          required
          :placeholder="'Nhập thời gian làm bài'"
          v-model="duration"
        ></v-text-field>
      </div>

      <div class="action">
        <v-btn @click="cancelCreateForm" color="#fcfcfc">Huỷ</v-btn>
        <v-btn @click="submitCreateForm">Thêm</v-btn>
      </div>
    </div>
  </div>
  <v-table fixed-header height="450px" class="test-table">
    <thead>
      <tr>
        <th class="text-center">ID</th>
        <th class="text-center">Môn học</th>
        <th class="text-center">Ngày tạo</th>
        <th class="text-center">Ngày mở đề</th>
        <th class="text-center">Tổng điểm</th>
        <th class="text-center">Thời gian làm bài (phút)</th>
        <th class="text-center">Hành động</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="test in tests" :key="test.id">
        <td @click="getTestDetailRoutePath(test.id)">
          <nuxt-link to="/teacher/tests/detail" class="link">{{
            test.id
          }}</nuxt-link>
        </td>
        <td>{{ test.subjectTitle }}</td>
        <td>{{ test.createdAt }}</td>
        <td>{{ test.testDay }}</td>
        <td>{{ test.totalPoint }}</td>
        <td>{{ test.duration }}</td>
        <td class="action">
          <v-icon size="small" class="me-2"> mdi-pencil </v-icon>
          <v-icon size="small"> mdi-delete </v-icon>
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

.test-table {
  cursor: pointer;
  text-align: center;
}

.form-create {
  margin-bottom: 16px;

  > .form {
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    display: flex;
    margin: 16px 0;
  }
  > .action {
    display: flex;
    gap: 24px;
    color: #fff;
  }
}

.link {
  text-decoration: none;
}
</style>
