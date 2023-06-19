<script lang="ts" setup>
import { ExamResult } from "@/types";
const resultsList: ExamResult[] = [
  {
    id: 1,
    examCompletionDate: "2023-06-01",
    examName: "ETS TOEIC 2020 Test 7",
    result: "73/100",
    examTime: "29:02",
  },
  {
    id: 2,
    examCompletionDate: "2023-02-03",
    examName: "ETS TOEIC 2020 Test 9",
    result: "78/100",
    examTime: "12:45",
  },
  {
    id: 3,
    examCompletionDate: "2023-06-01",
    examName: "ETS TOEIC 2020 Test 10",
    result: "63/100",
    examTime: "49:45",
  },
  {
    id: 4,
    examCompletionDate: "2023-09-01",
    examName: "ETS TOEIC 2020 Test 2",
    result: "50/100",
    examTime: "45:02",
  },
];
</script>

<template>
  <result-list :results-list="resultsList" />
</template>
