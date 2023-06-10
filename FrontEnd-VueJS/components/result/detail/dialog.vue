<script lang="ts" setup>
const props = defineProps<{
  isOpenDialog: boolean;
  closeDialog: () => void;
  resultDetailQuestion: any;
}>();

const answerKey = computed(() => props.resultDetailQuestion.answerKey);
</script>

<template>
  <div class="dialog">
    <v-dialog :model-value="isOpenDialog" width="400px">
      <v-card>
        <v-card-text>
          <h3 class="header">
            Đáp án chi tiết câu {{ resultDetailQuestion.order }}
          </h3>
          <div class="question">
            <div class="content">
              <span class="text"> {{ resultDetailQuestion.question }} </span>
              <div class="answers">
                <v-radio-group v-model="answerKey">
                  <v-radio
                    :label="resultDetailQuestion.answer1"
                    value="A"
                  ></v-radio>
                  <v-radio
                    :label="resultDetailQuestion.answer2"
                    value="B"
                  ></v-radio>
                  <v-radio
                    :label="resultDetailQuestion.answer3"
                    value="C"
                  ></v-radio>
                  <v-radio
                    :label="resultDetailQuestion.answer4"
                    value="D"
                  ></v-radio>
                </v-radio-group>
              </div>
            </div>
          </div>

          <span v-if="!resultDetailQuestion.isCorrect" class="title"
            >Đáp án đúng là: {{ resultDetailQuestion.resultKey }}</span
          >

          <div v-if="!resultDetailQuestion.isCorrect" class="key">
            Lời giải: {{ resultDetailQuestion.titleKey }}
          </div>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" block @click="closeDialog">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<style lang="scss" scoped>
.header {
  margin-bottom: 16px;
}
.title {
  color: #3cb46e;
}
</style>
