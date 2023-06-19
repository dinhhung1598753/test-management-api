<script lang="ts" setup>
import { Question, stringToBoolean } from "@/types";

const props = defineProps({
  questions: {
    type: Array<Question>,
    default: () => [{}],
  },
});
const { questions } = toRefs(props);
const questionItem = ref(null);
const isShowDetailQuestion = ref(false);

const detailQuestion = (question: any) => {
  isShowDetailQuestion.value = true;
  questionItem.value = question;
};
</script>

<template>
  <div class="list-questions">
    <v-table fixed-header height="500px">
      <thead>
        <tr>
          <th class="text-left">ID</th>
          <th class="text-left">Câu hỏi</th>
          <th class="text-left">Độ khó</th>
          <th class="text-left">Chi tiết</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="question in questions" :key="question.id">
          <td>{{ question.id }}</td>
          <td>{{ question.topicText }}</td>
          <td>{{ question.level }}</td>
          <td>
            <span class="detail" @click="detailQuestion(question)"
              >Xem chi tiết</span
            >
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>

  <div class="dialog-create-question">
    <v-row justify="center">
      <v-dialog v-model="isShowDetailQuestion" persistent width="1024">
        <v-card>
          <v-card-text>
            <v-container>
              <v-row>
                <v-col cols="6">
                  <span>ID</span>
                  <v-text-field>{{ questionItem.id }}</v-text-field>
                </v-col>
                <v-col cols="6">
                  <span>Mức độ</span>
                  <v-text-field>{{ questionItem.level }}</v-text-field>
                </v-col>
                <v-col cols="12">
                  <span>Câu hỏi</span>
                  <v-text-field>{{ questionItem.topicText }}</v-text-field>
                </v-col>
                <v-col cols="12">Đáp án </v-col>
                <v-col
                  cols="12"
                  v-for="answer in questionItem.answers"
                  :key="answer.id"
                >
                  <div class="wrap">
                    <v-text-field class="answer">{{
                      answer.content
                    }}</v-text-field>
                    <v-checkbox
                      label="Đáp án đúng"
                      class="checkbox"
                      :model-value="stringToBoolean(answer.isCorrected)"
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
              @click="isShowDetailQuestion = false"
            >
              Đóng
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-row>
  </div>
</template>

<style lang="scss" scoped>
.detail {
  color: #437ed0;
  cursor: pointer;
}

.wrap {
  display: flex;
  align-items: center;
  gap: 32px;

  > .answer {
    flex: 2;
  }
  > .checkbox {
    flex: 1;
  }
}
</style>
