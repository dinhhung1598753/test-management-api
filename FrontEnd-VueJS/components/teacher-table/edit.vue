<script lang="ts" setup>
const name = ref("");
const props = defineProps({
  isEditTeacher: {
    type: Boolean,
    default: false,
  },
  teacherById: {
    type: Object,
    default: () => ({}),
  },
});

const { teacherById } = toRefs(props);

const emit = defineEmits<{
  (e: "edit", data: any): void;
  (e: "close"): void;
}>();

const closeDialog = () => {
  emit("close");
};

const teacher = computed(() => props.teacherById);

const editTeacher = () => {
  emit("edit", {
    teacher,
  });
};
</script>
<template>
  <div class="dialog-edit-teacher">
    <v-row justify="center">
      <v-dialog :model-value="isEditTeacher" persistent width="1024">
        <v-card>
          <v-card-title>
            <span class="text-h5">Sửa thông tin giáo viên</span>
          </v-card-title>
          <v-card-text>
            <v-container>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    required
                    v-model="teacher.fullName"
                    :placeholder="'Nhập họ và tên'"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    required
                    v-model="teacher.code"
                    :placeholder="'Nhập MSSV'"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    required
                    v-model="teacher.birthday"
                    :placeholder="'Nhập ngày sinh'"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    required
                    v-model="teacher.gender"
                    :placeholder="'Nhập giới tính'"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    required
                    v-model="teacher.phoneNumber"
                    :placeholder="'Nhập số điện thoại'"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    required
                    v-model="teacher.email"
                    :placeholder="'Nhập email'"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue-darken-1" variant="text" @click="closeDialog">
              Huỷ
            </v-btn>
            <v-btn color="blue-darken-1" variant="text" @click="editTeacher">
              Lưu
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-row>
  </div>
</template>

<style lang="scss" scoped></style>
