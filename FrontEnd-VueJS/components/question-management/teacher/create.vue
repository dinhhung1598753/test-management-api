<script lang="ts" setup>
import { Subject, LEVEL } from "@/types";
import { useQuestionStore } from "@/stores/question";
import { useSubjectStore } from "@/stores/subject";

const questionStore = useQuestionStore();
const subjectStore = useSubjectStore();

const props = defineProps({
  subjects: {
    type: Array<Subject>,
    default: () => [{}],
  },
});
const { subjects } = toRefs(props);
const subjectCode = ref("");

watch(subjectCode, () => {
  subjectStore.getChapters(subjectCode.value);
});

const chapters = computed(() => {
  return subjectStore.chapters;
});

const levels = computed(() => {
  return LEVEL;
});
const topicText = ref("");
const chapterId = ref(1);
const level = ref("");
const topicImage = ref("");
const answers = ref([
  { content: "", isCorrected: "" },
  { content: "", isCorrected: "" },
  { content: "", isCorrected: "" },
  { content: "", isCorrected: "" },
]);

const isCreateQuestion = ref(false);

const submit = async () => {
  const res = await questionStore.createQuestion({
    subjectCode: subjectCode.value,
    chapterId: chapterId.value,
    topicText: topicText.value,
    topicImage: topicImage.value,
    level: level.value,
    answers: answers.value.map((item) => ({
      ...item,
      isCorrected: item.isCorrected ? "true" : "false",
    })),
  });
  // await questionStore.getQuestions();
  isCreateQuestion.value = false;
};

const createQuestion = () => {
  isCreateQuestion.value = true;
};
</script>

<template>
  <div class="question-management">
    <div class="action">
      <v-btn @click="createQuestion"
        ><v-icon icon="mdi-plus" />Thêm mới câu hỏi</v-btn
      >
      <v-file-input
        clearable
        label="Import bộ câu hỏi"
        variant="underlined"
      ></v-file-input>
    </div>
    <div class="dialog-create-question">
      <v-row justify="center">
        <v-dialog v-model="isCreateQuestion" persistent width="1024">
          <v-card>
            <v-card-title>
              <span class="text-h5">Thêm mới câu hỏi</span>
            </v-card-title>
            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12" sm="6" md="4">
                    <v-select
                      label="Môn học"
                      :items="subjects"
                      class="select"
                      item-title="title"
                      item-value="code"
                      v-model="subjectCode"
                      :variant="'outlined'"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-select
                      label="Chương"
                      :items="chapters"
                      item-title="order"
                      item-value="id"
                      class="select"
                      v-model="chapterId"
                      :variant="'outlined'"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-select
                      label="Độ khó"
                      :items="levels"
                      item-title="label"
                      item-value="key"
                      class="select"
                      v-model="level"
                      :variant="'outlined'"
                    ></v-select>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      required
                      v-model="topicText"
                      :placeholder="'Nhập câu hỏi'"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">Đáp án </v-col>

                  <v-col
                    cols="12"
                    v-for="(answer, index) in answers"
                    :key="index"
                  >
                    <div class="wrap">
                      <v-text-field
                        required
                        v-model="answer.content"
                        :placeholder="'Nhập đáp án'"
                      ></v-text-field>
                      <v-checkbox
                        v-model="answer.isCorrected"
                        label="Đáp án đúng"
                      ></v-checkbox>
                    </div>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isCreateQuestion = false"
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

<style lang="scss" scoped>
.question-management {
  > .action {
    width: 600px;
    display: flex;
    align-items: center;
    gap: 60px;
    overflow: hidden;
  }
}

.search-question-list {
  display: flex;
  gap: 32px;
  margin: 32px 0;
  justify-content: start;
  align-items: center;
}
.list-questions {
  margin-top: 16px;
  cursor: pointer;
}

.wrap {
  display: flex;
  align-items: center;
}
</style>
