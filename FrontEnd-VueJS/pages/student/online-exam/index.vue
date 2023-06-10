<script lang="ts" setup>
// TODO
const questions = [
  {
    id: 1,
    order: 1,
    question: "Why does the man want to buy Ms. Jefferson some flowers?",
    answers: [
      {
        id: 1,
        label: "A. She was promoted.",
      },
      {
        id: 2,
        label: "B. She won an award.",
      },
      {
        id: 3,
        label: "C. She is moving.",
      },
      {
        id: 4,
        label: "D. She is retiring.",
      },
    ],
  },
  {
    id: 2,
    order: 2,
    question: "Why does the man want to buy Ms. Jefferson some flowers?",
    answers: [
      {
        id: 1,
        label: "A. She was promoted.",
      },
      {
        id: 2,
        label: "B. She won an award.",
      },
      {
        id: 3,
        label: "C. She is moving.",
      },
      {
        id: 4,
        label: "D. She is retiring.",
      },
    ],
  },
  {
    id: 3,
    order: 3,
    question: "Why does the man want to buy Ms. Jefferson some flowers?",
    answers: [
      {
        id: 1,
        label: "A. She was promoted.",
      },
      {
        id: 2,
        label: "B. She won an award.",
      },
      {
        id: 3,
        label: "C. She is moving.",
      },
      {
        id: 4,
        label: "D. She is retiring.",
      },
    ],
  },
];

const minutes = ref(0);
const seconds = ref(0);

const formatTime = (time: number) => {
  return time < 10 ? `0${time}` : `${time}`;
};

const incrementTime = () => {
  seconds.value++;

  if (seconds.value === 60) {
    seconds.value = 0;
    minutes.value++;
  }
};

onMounted(() => {
  setInterval(() => {
    incrementTime();
  }, 1000);
});
</script>

<template>
  <div class="heading">
    <h2 class="name">C17 IELTS listening test 1</h2>
    <button class="button">Thoát <v-icon icon="mdi-arrow-right" /></button>
  </div>
  <div class="test-wrapper">
    <div class="test-content">
      <div
        class="question-wrapper"
        v-for="question in questions"
        :key="question.id"
      >
        <div class="order">{{ question.order }}</div>
        <div class="content">
          <span class="text">
            {{ question.question }}
          </span>
          <div class="answers">
            <v-radio-group
              class="radio"
              v-for="answer in question.answers"
              :key="answer.id"
            >
              <v-radio :label="answer.label" :value="answer.id"></v-radio>
            </v-radio-group>
          </div>
        </div>
      </div>
    </div>
    <div class="test-navigation">
      <h3 class="timeleft">Thời gian làm bài:</h3>
      <div class="time">
        {{ `${formatTime(minutes)}:${formatTime(seconds)}` }}
      </div>
      <button class="submit">Nộp bài</button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.heading {
  display: flex;
  gap: 8px;
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
  > .button {
    width: 80px;
    height: 34px;
    border: 1px solid $color-gray;
    border-radius: 4px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    color: #1e2022;
    background-color: #e8f2ff;
    border-color: #35509a;
  }
}

.test-wrapper {
  display: flex;
  flex-direction: row;
  position: relative;
  gap: 32px;
}

.test-content {
  flex: 5;
  background-color: $color-white;
  padding: 1rem;
  border-radius: 0.65rem;
  border: 1px solid #e0e0e0;
  box-shadow: 0 4px 0 0 rgba(143, 156, 173, 0.2);
}

.test-navigation {
  text-align: center;
  flex: 1;
  will-change: position, transform;
  padding: 1rem;
  background-color: $color-white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.08);

  > .time {
    font-weight: 600;
    font-size: 24px;
  }

  > .submit {
    height: 38px;
    width: 138px;
    border: 1px solid #35509a;
    color: #35509a;
    border-color: #35509a;
    font-size: 16px;
    border-radius: 4px;
    font-weight: 500;
    margin-top: 32px;
  }

  > .submit:hover {
    color: $color-white;
    background-color: #35509a;
    border-color: #35509a;
  }
}

.question-wrapper {
  display: flex;
  margin-bottom: 24px;

  > .order {
    margin-right: 8px;
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
  > .content {
    width: 100%;
  }
  > .content > .text {
    margin-bottom: 8px;
    margin-left: 12px;
    font-size: 16px;
  }
  > .content > .answers {
    position: relative;
    display: block;
  }
  > .content > .answers > .radio {
    :deep(.v-input__control) {
      font-size: 14px;
    }

    :deep(.v-selection-control .v-label) {
      font-size: 14px;
      color: #000;
    }
  }
}
</style>
