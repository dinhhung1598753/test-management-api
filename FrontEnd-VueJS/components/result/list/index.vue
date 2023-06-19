<script lang="ts" setup>
import { ExamResult } from "@/types";

const props = defineProps({
  resultsList: {
    type: Array<ExamResult>,
    default: () => [{}],
  },
});
</script>

<template>
  <h2 class="title">Thống kê kết quả luyện thi</h2>
  <div class="result-score-box-wrapper">
    <div class="score-box">
      <span class="icon"><v-icon icon="mdi-book-open-variant"></v-icon></span>
      <span class="text">Số đề đã làm</span>
      <span class="core">8</span>
    </div>
    <div class="score-box">
      <span class="icon"><v-icon icon="mdi-av-timer"></v-icon></span>
      <span class="text">Thời gian luyện thi</span>
      <span class="core">150 phút</span>
    </div>
    <div class="score-box">
      <span class="icon"><v-icon icon="mdi-pencil"></v-icon></span>
      <span class="text">Độ chính xác</span>
      <span class="core">85%</span>
    </div>
    <div class="score-box">
      <span class="icon"
        ><v-icon icon="mdi-checkbox-marked-circle"></v-icon
      ></span>
      <span class="text">Điểm trung bình</span>
      <span class="core">8.5</span>
    </div>
  </div>
  <div class="list-results">
    <h3>Danh sách đề thi đã làm:</h3>
    <v-table>
      <thead>
        <tr class="row-head">
          <th class="cell text-center">Ngày làm</th>
          <th class="cell text-center">Đề thi</th>
          <th class="cell text-center">Kết quả</th>
          <th class="cell text-center">Thời gian làm bài</th>
          <th class="cell text-center"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="result in resultsList" :key="result.id">
          <td class="text-center">{{ result.examCompletionDate }}</td>
          <td class="text-center">{{ result.examName }}</td>
          <td class="text-center">{{ result.result }}</td>
          <td class="text-center">{{ result.examTime }}</td>
          <td class="text-center">
            <a href="/student/tests/result-detail">Xem chi tiết</a>
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>
</template>

<style lang="scss" scoped>
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.result-score-box-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  margin: 32px 0;

  > .score-box {
    min-width: 200px;
    max-width: 200px;
    display: flex;
    gap: 4px;
    flex-direction: column;
    background-color: $color-white;
    padding: 16px;
    border: 1px solid #efefef;
    box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.05);
    border-radius: 0.65rem;
    margin-bottom: 1rem;
    align-items: center;
    justify-content: flex-start;
  }

  > .score-box > .text {
    color: #677788;
    font-size: 16px;
  }

  > .score-box > .core {
    font-size: 20px;
    font-weight: 600;
    color: $color-black;
  }
}
</style>
