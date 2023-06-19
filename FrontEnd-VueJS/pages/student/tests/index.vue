<script lang="ts" setup>
const checkbox1 = ref(true); // TODO
const checkbox2 = ref(false); // TODO
const checkbox3 = ref(false); // TODO
const checkbox4 = ref(false); // TODO

const tab = ref(null);
</script>
<template>
  <h2 class="title">Thư viện đề thi</h2>
  <div class="online-exam-information">
    <div class="header">
      <h2 class="name">C17 IELTS listening test 1</h2>
      <div class="options">
        <p class="item -active">Thông tin đề thi</p>
        <p class="item">Đáp án/transcript</p>
      </div>
    </div>

    <div class="tab-content">
      <div class="item">
        <h3 class="label">Bộ đề thi:</h3>
        <h3 class="content">IELTS C17 Full Test 1</h3>
      </div>
      <div class="detail">
        <v-icon icon="mdi-clock"></v-icon>
        <span class="label">Thời gian làm bài:</span>
        <span class="content">40 phút</span>
      </div>
      <div class="detail">
        <v-icon icon="mdi-pencil"></v-icon>
        <span class="label">Số lượng:</span>
        <span class="content">20 câu</span>
      </div>
    </div>

    <div class="nav-info">
      <v-card class="card">
        <v-tabs v-model="tab" centered stacked class="tabs">
          <v-tab value="tab-1"> Luyện tập </v-tab>

          <v-tab value="tab-2"> Làm full test </v-tab>
        </v-tabs>

        <v-window v-model="tab">
          <v-window-item :value="'tab-1'">
            <v-card>
              <v-card-text>
                <div class="tips">
                  <v-icon icon="mdi-lightbulb-on-outline" />
                  Pro tips: Hình thức luyện tập từng phần và chọn mức thời gian
                  phù hợp sẽ giúp bạn tập trung vào giải đúng các câu hỏi thay
                  vì phải chịu áp lực hoàn thành bài thi.
                </div>
                <div class="choice">
                  <p class="heading">Chọn phần thi bạn muốn làm</p>
                  <v-container fluid>
                    <div class="wrap">
                      <v-checkbox
                        class="checkbox"
                        v-model="checkbox1"
                        :label="'Chương 1'"
                      ></v-checkbox>
                      <span class="count">Gồm 10 câu hỏi</span>
                    </div>
                    <div class="wrap">
                      <v-checkbox
                        v-model="checkbox2"
                        :label="'Chương 2'"
                      ></v-checkbox>
                      <span class="count">Gồm 10 câu hỏi</span>
                    </div>
                    <div class="wrap">
                      <v-checkbox
                        v-model="checkbox3"
                        :label="'Chương 3'"
                      ></v-checkbox>
                      <span class="count">Gồm 10 câu hỏi</span>
                    </div>
                    <div class="wrap">
                      <v-checkbox
                        v-model="checkbox4"
                        :label="'Chương 4'"
                      ></v-checkbox>
                      <span class="count">Gồm 10 câu hỏi</span>
                    </div>
                  </v-container>
                </div>

                <div class="time">
                  <p class="label">
                    Giới hạn thời gian (Để trống để làm bài không giới hạn)
                  </p>
                  <v-select
                    label="--Chọn thời gian--"
                    :items="['10 phút', '30 phút', '60 phút', '120 phút']"
                    class="select"
                    :variant="'outlined'"
                  ></v-select>

                  <v-btn class="button">Luyện tập</v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-window-item>
          <v-window-item :value="'tab-2'">
            <v-card>
              <v-card-text>
                <div class="warning">
                  <v-icon icon="mdi-exclamation" />
                  Sẵn sàng để bắt đầu làm full test? Để đạt được kết quả tốt
                  nhất, bạn cần dành ra 40 phút cho bài test này.
                </div>

                <div class="time">
                  <v-btn class="button">
                    <a class="link" :href="'/student/online-exam'"
                      >Bắt đầu thi</a
                    >
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-window-item>
        </v-window>
      </v-card>
    </div>
  </div>
</template>

<style scoped lang="scss">
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.online-exam-infomation {
  background-color: $color-white;
  padding: 1rem;
  border-radius: 0.65rem;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 0 0 rgba(143, 156, 173, 0.2);
}

.header {
  > .options {
    display: flex;
    margin-top: 8px;
    gap: 22px;
  }

  > .options > .item {
    cursor: pointer;
    padding: 4px 15px;
    background-color: #f8f9fa;
    border-radius: 25px;
    white-space: nowrap;
    color: #1a1a1a;
  }
  > .options > .item:hover {
    background-color: #e7eaf3;
  }
  > .options > .item.-active {
    background-color: #e8f2ff;
    color: #35509a;
    font-weight: 500;
  }
}
.tab-content {
  border-bottom: 1px solid #ccc;
  padding-bottom: 16px;
}
.tab-content > .item {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}
.tab-content > .detail {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.nav-info {
  margin-top: 32px;
}
.card {
  :deep(.v-card) {
    background-color: $color-white !important;
  }
}
.tabs {
  :deep(.v-tab.v-tab) {
    height: 42px;
  }
}

.tips {
  color: #1f5e39;
  background-color: #d8f0e2;
  border-color: #c8ead6;
  border-radius: 4px;
  padding: 8px;
}

.warning {
  color: #855a1f;
  background-color: #ffefd8;
  border-color: #ffe8c8;
  border-radius: 4px;
  padding: 8px;
}

.choice > .heading {
  margin-top: 12px;
  margin-bottom: -12px;
}
.wrap {
  :deep(.v-input--horizontal) {
    height: 46px;
  }

  > .count {
    margin-left: 12px;
  }
}

.time > .label {
  margin-bottom: 12px;
}

.time > .button {
  margin-top: 12px;
}
.link {
  text-decoration: none;
  background-color: $primary-color;
  color: $color-white;
}
</style>
