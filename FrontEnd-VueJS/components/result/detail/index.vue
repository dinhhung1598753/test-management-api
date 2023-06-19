<script lang="ts" setup>
const subjectName = ref("ETS TOEIC 2020 Test 7");
const mark = ref("80/100");
const percent = ref("80%");
const time = ref("1:29:02");
const isOpenDialog = ref(false);
const resultDetail = [
  {
    id: 1,
    order: 1,
    question: "Are you sure you want to?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "A",
    resultKey: "A",
    titleKey:
      " (84) The Clark Institute is offering Internet classes to train you on these new rules.Dịch: Viện Clark đang cung cấp các lớp học Internet để đào tạo bạn về các quy tắc mới này. Điều gì đang được quảng cáo?",
    isCorrect: true,
  },
  {
    id: 2,
    order: 2,
    question: "Are you sure you want to?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "A",
    resultKey: "B",
    titleKey: "Vì nó đúng hihih",
    isCorrect: false,
  },
  {
    id: 3,
    order: 3,
    question: "Are you sure you want to?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "C",
    resultKey: "C",
    titleKey: "Vì nó đúng hihih",
    isCorrect: true,
  },
  {
    id: 4,
    order: 4,
    question: "Are you sure you want to?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "B",
    resultKey: "B",
    titleKey: "Vì nó đúng hihih",
    isCorrect: true,
  },
  {
    id: 5,
    order: 5,
    question: "Are you sure you want to?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "A",
    resultKey: "C",
    titleKey: "Vì nó đúng hihih",
    isCorrect: false,
  },
  {
    id: 6,
    order: 6,
    question: "What still needs to be confirmed?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "C",
    titleKey: "Vì nó đúng hihih",
    resultKey: "C",
    isCorrect: true,
  },
  {
    id: 7,
    order: 7,
    answerKey: "B",
    question: "What still needs to be confirmed?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    resultKey: "B",
    titleKey: "Vì nó đúng hihih",
    isCorrect: true,
  },
  {
    id: 8,
    order: 8,
    question: "Are you sure you want to?",
    answer1: "A. The event location",
    answer2: "B. The registration fee",
    answer3: "C. The start time",
    answer4: "D. The catering arrangements",
    answerKey: "A",
    resultKey: "C",
    titleKey: "Vì nó đúng hihih",
    isCorrect: false,
  },
];
const resultDetailQuestion = ref(null);

const openResultDetail = (result: any) => {
  resultDetailQuestion.value = result;
  isOpenDialog.value = true;
};

const closeDialog = () => {
  isOpenDialog.value = false;
};
</script>

<template>
  <h2 class="title">Kết quả luyện tập: {{ subjectName }}</h2>

  <div class="result-wrapper">
    <div class="navigation">
      <div class="item">
        <v-icon icon="mdi-pencil"></v-icon>
        <span class="text">Kết quả làm bài</span>
        <span class="score">{{ mark }}</span>
      </div>
      <div class="item">
        <v-icon icon="mdi-check"></v-icon>
        <span class="text">Độ chính xác</span>
        <span class="score">{{ percent }}</span>
      </div>
      <div class="item">
        <v-icon icon="mdi-av-timer"></v-icon>
        <span class="text">Thời gian hoàn thành</span>
        <span class="score">{{ time }}</span>
      </div>
    </div>

    <div class="score-box">
      <div class="item">
        <v-icon
          icon="mdi-checkbox-marked-circle-outline"
          class="icon -correct"
        ></v-icon>
        <span class="text -correct">Trả lời đúng</span>
        <span class="count">80</span>
        <span class="text">câu hỏi</span>
      </div>
      <div class="item">
        <v-icon icon="mdi-close-circle-outline" class="icon -wrong"></v-icon>
        <span class="text -wrong">Trả lời sai</span>
        <span class="count">20</span>
        <span class="text">câu hỏi</span>
      </div>
      <div class="item">
        <v-icon icon="mdi-alert-circle-outline" class="icon -disabled"></v-icon>
        <span class="text -disabled">Bỏ qua</span>
        <span class="count">0</span>
        <span class="text">câu hỏi</span>
      </div>
    </div>
  </div>

  <h3 class="header">Phân tích chi tiết</h3>
  <div class="result-detail">
    <div class="result-answers-list">
      <div
        class="result-answers-item"
        v-for="result in resultDetail"
        :key="result.id"
      >
        <span class="number">{{ result.order }}</span>
        <span class="resultkey">{{ result.resultKey }}</span>
        <span class="answerkey" :class="{ '-wrong': !result.isCorrect }"
          >{{ result.answerKey }}:</span
        >
        <v-icon
          v-if="result.isCorrect"
          icon="mdi-check"
          class="correct"
        ></v-icon>
        <v-icon
          v-if="!result.isCorrect"
          icon="mdi-close"
          class="wrong"
        ></v-icon>
        <span class="detail" @click="openResultDetail(result)">[Chi tiết]</span>
      </div>
    </div>
  </div>
  <result-detail-dialog
    :isOpenDialog="isOpenDialog"
    :close-dialog="closeDialog"
    :resultDetailQuestion="resultDetailQuestion"
  />
</template>
<style lang="scss" scoped>
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.result-wrapper {
  display: flex;
  margin-top: 32px;
}

.navigation {
  font-size: 16px;
  gap: 12px;
  padding: 1.5rem 1rem;
  background-color: #f8f9fa;
  border: 1px solid #efefef;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  border-radius: 4px;

  > .item {
    display: flex;
    align-items: center;
    gap: 6px;
    justify-content: space-between;
  }

  > .item > .score {
    font-weight: bold;
  }
}

.score-box {
  display: flex;
  gap: 60px;
  margin-left: 32px;
  > .item {
    width: 160px;
    background-color: $color-white;
    padding: 8px;
    border: 1px solid #efefef;
    box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.05);
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
  }

  > .item > .text.-correct {
    color: green;
    font-size: 16px;
  }
  > .item > .text.-wrong {
    color: red;
    font-size: 16px;
  }
  > .item > .text.-disabled {
    color: gray;
    font-size: 16px;
  }

  > .item > .count {
    font-size: 20px;
    font-weight: 600;
  }
}
.item > .icon.-correct {
  color: green;
}
.item > .icon.-wrong {
  color: red;
}
.item > .icon.-disabled {
  color: gray;
}

.header {
  margin-top: 32px;
}
.result-detail {
  columns: 2;
  -webkit-columns: 2;
}

.result-answers-list {
  display: flex;
  margin-top: 16px;
  flex-direction: column;
  gap: 12px;
}

.result-answers-item {
  display: flex;
  gap: 10px;
  align-items: center;
  > .number {
    border-radius: 50%;
    background-color: #e8f2ff;
    color: #35509a;
    width: 35px;
    height: 35px;
    line-height: 35px;
    font-size: 15px;
    text-align: center;
    display: inline-block;
  }

  > .answerkey {
    font-style: italic;
  }

  > .answerkey.-wrong {
    text-decoration: line-through;
  }
  > .detail {
    color: #35509a;
    cursor: pointer;
  }
}

.correct {
  color: green;
}

.wrong {
  color: red;
}
</style>
