<script lang="ts" setup>
import { Question } from "@/types";
import { useQuestionStore } from "@/stores/question";
import { useSubjectStore } from "@/stores/subject";

const questionStore = useQuestionStore();
const subjectStore = useSubjectStore();
const questions = computed(() => questionStore.questions);

//get subjects
const res = await subjectStore.getSubjects();
const subjects = computed(() => subjectStore.subjects);

// get questions
const fetchQuestionsBySubject = async (code: string) => {
  const res = await questionStore.getQuestions(code);
};

const chapters = ["Chương 1", "Chương 2", "Chương 3"];
const levels = ["Dễ", "Trung bình", "Khó"];

const isCreateQuestion = ref(false);
const isDeleteQuestion = ref(false);

const createQuestion = () => {
  isCreateQuestion.value = true;
};
const authUser = ref("teacher");
const deleteQuestion = () => {
  isDeleteQuestion.value = true;
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
    <v-btn @click="uploadFile">submit</v-btn>
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
    <question-management-list
      :questions="questions"
      :auth-user="authUser"
      @create="createQuestion"
      @delete="deleteQuestion"
    />
    <question-management-update />
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
                      :variant="'outlined'"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-select
                      label="Bộ đề"
                      :items="chapters"
                      class="select"
                      :variant="'outlined'"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-select
                      label="Độ khó"
                      :items="levels"
                      class="select"
                      :variant="'outlined'"
                    ></v-select>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      required
                      :placeholder="'Nhập câu hỏi'"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">Đáp án </v-col>
                  <v-col cols="12">
                    <div class="wrap">
                      <v-text-field
                        required
                        :placeholder="'Nhập đáp án'"
                      ></v-text-field>
                      <v-checkbox label="Đáp án đúng"></v-checkbox>
                    </div>
                  </v-col>
                  <v-col cols="12">
                    <div class="wrap">
                      <v-text-field
                        required
                        :placeholder="'Nhập đáp án'"
                      ></v-text-field>
                      <v-checkbox label="Đáp án đúng"></v-checkbox>
                    </div>
                  </v-col>
                  <v-col cols="12">
                    <div class="wrap">
                      <v-text-field
                        required
                        :placeholder="'Nhập đáp án'"
                      ></v-text-field>
                      <v-checkbox label="Đáp án đúng"></v-checkbox>
                    </div>
                  </v-col>
                  <v-col cols="12">
                    <div class="wrap">
                      <v-text-field
                        required
                        :placeholder="'Nhập đáp án'"
                      ></v-text-field>
                      <v-checkbox label="Đáp án đúng"></v-checkbox>
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
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isCreateQuestion = false"
              >
                Lưu
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-row>
    </div>
    <div class="dialog-delete-question">
      <v-row justify="center">
        <v-dialog v-model="isDeleteQuestion" persistent width="500">
          <v-card>
            <v-card-text>
              <h3>Bạn có chắc chắn muốn xoá câu hỏi không?</h3>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isDeleteQuestion = false"
              >
                Huỷ
              </v-btn>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="isDeleteQuestion = false"
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
