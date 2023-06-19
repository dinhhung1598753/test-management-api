<script lang="ts" setup>
import { Question } from "@/types";
import { useQuestionStore } from "@/stores/question";
import { useSubjectStore } from "@/stores/subject";
import { useTestStore } from "@/stores/test";

const isEditQuestion = ref(false);

const questionStore = useQuestionStore();
const subjectStore = useSubjectStore();
const testsStore = useTestStore();

const subjectCode = ref("");
const questions = computed(() => questionStore.questions);

const questionById = ref({});
//get subjects
const res = await subjectStore.getSubjects();
const subjects = computed(() => subjectStore.subjects);

// get questions
const fetchQuestionsBySubject = async (code: string) => {
  const res = await questionStore.getQuestions(code);
};

// get chapters
const fetchChapterBySubject = async (code: string) => {
  const res = await subjectStore.getChapters(code);
};

// create test by checkbox questions
const questionIds = ref([]);
const testDay = ref("");
const duration = ref(0);

const createTestByCheckbox = async () => {
  const res = await testsStore.createTestCheckbox(
    questionIds.value,
    testDay.value,
    +duration.value
  );
};

const openDialogEditQuestion = (question: object) => {
  questionById.value = question;
  isEditQuestion.value = true;
};

const closeDialog = () => {
  isEditQuestion.value = false;
};

// TODO
const chapterId = ref(53);
const editQuestion = async (e: any) => {
  const id = e.question.value.id;
  const topicText = e.question.value.topicText;
  const level = e.question.value.level;
  const answers = e.question.value.answers.map((item: any) => ({
    ...item,
    isCorrected: item.isCorrected ? "true" : "false",
  }));
  const topicImage = e.question.value.topicImage;

  const res = await questionStore.updateById(id, {
    chapterId: +chapterId.value,
    topicText,
    level,
    answers,
    topicImage,
  });
};

const deleteQuestion = async (id: number) => {
  const res = await questionStore.deleteById(id);
};
</script>

<template>
  <h2 class="title">Ngân hàng câu hỏi</h2>

  <question-management-teacher-create :subjects="subjects" />

  <div class="search-question-list">
    <v-autocomplete
      clearable
      label="Nhập tên môn"
      :items="subjects"
      item-title="title"
      item-value="code"
      v-model="subjectCode"
    ></v-autocomplete>
    <v-btn @click="fetchQuestionsBySubject(subjectCode)">Tìm kiếm</v-btn>
  </div>
  <div class="list-questions">
    <v-table fixed-header height="400px">
      <thead>
        <tr>
          <th class="text-left">Tạo bài test</th>
          <th class="text-left">ID</th>
          <th class="text-left">Câu hỏi</th>
          <th class="text-left">Độ khó</th>
          <th class="text-left">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="question in questions" :key="question.id">
          <td><v-checkbox-btn /></td>
          <td>{{ question.id }}</td>
          <td>{{ question.topicText }}</td>
          <td>{{ question.level }}</td>
          <td class="action">
            <v-icon
              size="small"
              class="me-2"
              @click="openDialogEditQuestion(question)"
            >
              mdi-pencil
            </v-icon>
            <v-icon size="small" @click="deleteQuestion(question.id)">
              mdi-delete
            </v-icon>
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>

  <question-management-teacher-edit
    :isEditQuestion="isEditQuestion"
    :subjects="subjects"
    :questionById="questionById"
    @close="closeDialog"
    @edit="editQuestion"
  />

  <div class="create-test">
    <h3>Tạo bài test random</h3>
    <div class="wrapper">
      <v-text-field
        v-model="testDay"
        label="Nhập ngày mở đề"
        required
      ></v-text-field>
      <v-text-field
        v-model="duration"
        label="Nhập thời gian làm bài"
        required
      ></v-text-field>
      <v-btn @click="createTestByCheckbox">Tạo đề thi</v-btn>
    </div>
  </div>
</template>

<style scoped lang="scss">
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.search-question-list {
  display: flex;
  gap: 32px;
  margin: 32px 0;
  justify-content: start;
  align-items: center;
}

.create-test {
  margin-top: 32px;

  > .wrapper {
    margin-top: 16px;
    display: flex;
    justify-content: center;
    gap: 32px;
    align-items: center;
  }
}
</style>
