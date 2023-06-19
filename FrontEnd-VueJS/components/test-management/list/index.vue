<script lang="ts" setup>
import { Question, AUTH_USER } from "@/types";

const props = defineProps({
  questions: {
    type: Array<Question>,
    default: () => [{}],
  },
  authUser: {
    type: String,
    default: "",
  },
});
const { questions, authUser } = toRefs(props);

const emit = defineEmits<{
  (e: "create"): void;
  (e: "delete"): void;
}>();

const createQuestion = () => {
  emit("create");
};

const deleteQuestion = () => {
  emit("delete");
};

const isTeacher = computed(() => authUser.value === AUTH_USER.teacher);
</script>

<template>
  <div class="list-questions">
    <v-table fixed-header height="500px">
      <thead>
        <tr>
          <th v-if="isTeacher"></th>
          <th class="text-left">STT</th>
          <th class="text-left">Câu hỏi</th>
          <th class="text-left">Độ khó</th>
          <th v-if="isTeacher" class="text-left">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="question in questions" :key="question.id">
          <td v-if="isTeacher"><v-checkbox-btn /></td>
          <td>{{ question.id }}</td>
          <td>{{ question.topicText }}</td>
          <td>{{ question.level }}</td>
          <td class="action" v-if="isTeacher">
            <v-icon size="small" class="me-2" @click="createQuestion">
              mdi-pencil
            </v-icon>
            <v-icon size="small" @click="deleteQuestion"> mdi-delete </v-icon>
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>
</template>
