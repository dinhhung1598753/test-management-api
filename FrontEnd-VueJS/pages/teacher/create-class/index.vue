<script lang="ts" setup>
const tab = ref(null);
const time = ref("");
const numberOfExam = ref(0);
const isPublicExam = ref(false);
const subjects = ["Giải tích", "Lập trình C++", "Đại số", "CTDL & GT"];
</script>

<template>
  <h2 class="title">Tạo lớp thi</h2>

  <div class="nav-info">
    <v-card class="card">
      <v-tabs v-model="tab" centered stacked class="tabs">
        <v-tab value="tab-1"> Tạo lớp thi </v-tab>

        <v-tab value="tab-2"> Import đề thi </v-tab>
        <v-tab value="tab-3"> Import danh sách sinh viên </v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <v-window-item :value="'tab-1'">
          <div class="select-options">
            <v-select
              label="--Chọn môn học--"
              :items="subjects"
              class="select"
              :variant="'outlined'"
            ></v-select>
            <v-text-field
              v-model="time"
              label="Nhập thời gian làm bài"
              required
            ></v-text-field>
            <v-text-field
              v-model="numberOfExam"
              label="Nhập số lượng đề"
              required
            ></v-text-field>
          </div>
          <v-switch
            class="toggle"
            label="Public đề thi"
            color="primary"
            inset
            v-model="isPublicExam"
          ></v-switch>
        </v-window-item>
        <v-window-item :value="'tab-2'">
          <v-file-input
            clearable
            label="Chọn đề thi"
            class="file"
          ></v-file-input>
          <v-btn class="button">Import đề thi</v-btn>
        </v-window-item>
        <v-window-item :value="'tab-3'">
          <v-file-input
            clearable
            label="Chọn danh sách sinh viên"
            class="file"
          ></v-file-input>
          <v-btn class="button">Import danh sách sinh viên</v-btn>
        </v-window-item>
      </v-window>
    </v-card>
  </div>
</template>

<style lang="scss" scoped>
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.file {
  :deep(.v-input__control) {
    overflow: hidden;
    width: 400px;
  }

  :deep(.v-input__prepend) {
    margin-left: 12px;
  }
}

.toggle {
  :deep(.v-input__control) {
    padding-left: 16px;
  }
}
.button {
  margin-bottom: 32px;
  margin-left: 16px;
}

.select-options {
  padding: 16px;
  display: flex;
  gap: 16px;
}
</style>
