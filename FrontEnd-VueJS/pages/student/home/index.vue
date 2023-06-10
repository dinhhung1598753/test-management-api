<script lang="ts" setup>
const subjects = [
  { id: 1, label: "Tất cả", path: "/" },
  { id: 2, label: "IELTS Academic", path: "/" },
  { id: 3, label: "IELTS General", path: "/" },
  { id: 4, label: "TOEIC", path: "/" },
  { id: 5, label: "Tiếng Anh THPTQG", path: "/" },
  { id: 6, label: "Toán THPTQG", path: "/" },
  { id: 7, label: "Địa lí THPTQG", path: "/" },
  { id: 8, label: "Vật lí THPTQG", path: "/" },
  { id: 9, label: "Sinh học THPTQG", path: "/" },
  { id: 10, label: "Lịch sử THPTQG", path: "/" },
  { id: 11, label: "Toán cao cấp", path: "/" },
  { id: 12, label: "Giải tích", path: "/" },
];

const isAllSubjects = (item: any) => {
  return item === subjects[0];
};
</script>

<template>
  <h2 class="title">Chào mừng Nguyễn Ngọc Bảo Châm!</h2>
  <div class="subject-search">
    <!--TODO: Add some subjects from BE-->
    <h3 class="label">Thư viện đề thi</h3>
    <div class="test-exams">
      <ul class="nav">
        <li class="item" v-for="subject in subjects" :key="subject.id">
          <a
            class="link"
            :class="{ '-active': isAllSubjects(subject) }"
            :href="subject.path"
            >{{ subject.label }}</a
          >
        </li>
      </ul>
    </div>
    <div class="action">
      <v-text-field
        v-model="search"
        label="Nhập tên môn học..."
        single-line
        hide-details
        class="search"
      ></v-text-field>
      <v-select
        label="--Chọn bộ đề thi--"
        :items="['Chương 1', 'Chương 2', 'Chương 3', 'Chương 4']"
        class="select"
        :variant="'outlined'"
      ></v-select>
    </div>
    <v-btn class="button">Tìm kiếm</v-btn>
  </div>
  <h3 class="tested">Danh sách môn đã thi</h3>
  <div class="subject-list">
    <div class="item">
      <div class="subject">
        <span class="name">Tên môn học:</span>
        <span class="label">C17 IELTS listening test 1</span>
      </div>
      <div class="topic">
        <span class="name">Bộ đề:</span>
        <span class="label"> test 1</span>
      </div>
      <div class="deadline">
        <span class="name">Thời gian:</span>
        <span class="label"> 60 phút</span>
      </div>
      <div class="count">
        <span class="name">Số lượng câu:</span>
        <span class="label"> 60 câu</span>
      </div>
      <div class="action">
        <v-btn variant="outlined" class="search">
          <a class="link" :href="'/student/tests'">Xem chi tiết</a></v-btn
        >
      </div>
    </div>
    <div class="item">
      <div class="subject">
        <span class="name">Tên môn học:</span>
        <span class="label">C17 IELTS listening test 1</span>
      </div>
      <div class="topic">
        <span class="name">Bộ đề:</span>
        <span class="label"> test 1</span>
      </div>
      <div class="deadline">
        <span class="name">Thời gian:</span>
        <span class="label"> 60 phút</span>
      </div>
      <div class="count">
        <span class="name">Số lượng câu:</span>
        <span class="label"> 60 câu</span>
      </div>
      <div class="action">
        <v-btn variant="outlined" class="search">
          <a class="link" :href="'/student/tests'">Xem chi tiết</a></v-btn
        >
      </div>
    </div>
    <div class="item">
      <div class="subject">
        <span class="name">Tên môn học:</span>
        <span class="label">C17 IELTS listening test 1</span>
      </div>
      <div class="topic">
        <span class="name">Bộ đề:</span>
        <span class="label"> test 1</span>
      </div>
      <div class="deadline">
        <span class="name">Thời gian:</span>
        <span class="label"> 60 phút</span>
      </div>
      <div class="count">
        <span class="name">Số lượng câu:</span>
        <span class="label"> 60 câu</span>
      </div>
      <div class="action">
        <v-btn variant="outlined" class="search">
          <a class="link" :href="'/student/tests'">Xem chi tiết</a></v-btn
        >
      </div>
    </div>
    <div class="item">
      <div class="subject">
        <span class="name">Tên môn học:</span>
        <span class="label">C17 IELTS listening test 1</span>
      </div>
      <div class="topic">
        <span class="name">Bộ đề:</span>
        <span class="label"> test 1</span>
      </div>
      <div class="deadline">
        <span class="name">Thời gian:</span>
        <span class="label"> 60 phút</span>
      </div>
      <div class="count">
        <span class="name">Số lượng câu:</span>
        <span class="label"> 60 câu</span>
      </div>
      <div class="action">
        <v-btn variant="outlined" class="search">
          <a class="link" :href="'/student/tests'">Xem chi tiết</a></v-btn
        >
      </div>
    </div>
    <div class="item">
      <div class="subject">
        <span class="name">Tên môn học:</span>
        <span class="label">C17 IELTS listening test 1</span>
      </div>
      <div class="topic">
        <span class="name">Bộ đề:</span>
        <span class="label"> test 1</span>
      </div>
      <div class="deadline">
        <span class="name">Thời gian:</span>
        <span class="label"> 60 phút</span>
      </div>
      <div class="count">
        <span class="name">Số lượng câu:</span>
        <span class="label"> 60 câu</span>
      </div>
      <div class="action">
        <v-btn variant="outlined" class="search">
          <a class="link" :href="'/student/tests'">Xem chi tiết</a></v-btn
        >
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.title {
  padding-bottom: 16px;
  border-bottom: 1px solid $color-gray;
  margin-bottom: 12px;
}

.subject-search {
  background-color: #f8f9fa;
  padding: 16px;
  > .label {
    margin-bottom: 24px;
  }
}
.tested {
  margin-top: 32px;
  padding-left: 16px;
}
.test-exams > .nav {
  width: 80%;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  > .item {
    margin-top: 8px;
  }
  > .item > .link {
    text-decoration: none;
    color: $color-black;
    padding: 8px;
    border-radius: 20px;
    white-space: nowrap;
  }

  > .item > .link:hover {
    background-color: #e7eaf3;
  }

  > .item > .link.-active {
    color: #35509a;
    background-color: #86a3f452;
  }
}

.action {
  margin-top: 24px;
  display: flex;
  gap: 32px;
  > .search {
    :deep(.v-input--density-compact) {
      width: 500px;
    }
  }

  > .select {
    :deep(.v-input__control) {
      width: 300px;
    }
    :deep(.v-field--variant-filled .v-field__overlay) {
      background-color: #f8f9fa;
    }
  }
}

.button {
  margin-top: 12px;
}

.subject-list {
  display: flex;
  flex-wrap: wrap;
  margin-top: 12px;
  padding: 16px;
  gap: 16px;
  background-color: #f8f9fa;
  .item {
    border-radius: 8px;
    box-shadow: 0 2px 0 0 rgba(143, 156, 173, 0.2);
    border: 1px solid $color-gray;
    background-color: $color-white;
    padding: 16px;
    cursor: pointer;
  }

  .item:hover {
    box-shadow: 0 1px 2px 0 rgba(60, 64, 67, 0.2),
      0 2px 6px 2px rgba(60, 64, 67, 0.15);
    transform: translateY(-2px);
    transition: all 0.4s;
  }

  .item > .subject > .name,
  .item > .topic > .name,
  .item > .deadline > .name,
  .item > .count > .name {
    margin-right: 10px;
    font-weight: bold;
  }

  .item > .action {
    display: flex;
    justify-content: center;
    .link {
      text-decoration: none;
      color: #2196f3;
    }
  }
}
</style>
