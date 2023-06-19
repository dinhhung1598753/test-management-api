import { Question } from "@/types";
import { api } from "@/apis";

export const useQuestionStore = defineStore("question", () => {
  const questions = ref<Question[]>([]);
  const isCreating = ref(false);

  const getQuestions = async (code: string) => {
    const res = await api.get(`/question/list?code=${code}`).catch((err) => {
      console.log(err);
      return null;
    });
    questions.value = res?.data || [];
  };

  const createQuestion = async (data: {
    subjectCode: string;
    chapterId: number;
    topicText: string;
    topicImage: string;
    level: string;
    answers: {
      content: string;
      isCorrected: string;
    }[];
  }) => {
    const res = await api.post("/question/add", data).catch((err) => {});
  };

  const updateById = async (
    id: number,
    data: {
      chapterId: number;
      topicText: string;
      topicImage: string;
      level: string;
      answers: {
        content: string;
        isCorrected: string;
      }[];
    }
  ) => {
    const res = await api
      .put(`/question/update/${id}`, data)
      .catch((err) => {});
    return res;
  };

  const deleteById = async (id: number) => {
    const res = await api.delete(`question/disable/${id}`).catch(() => null);
    if (res !== null) {
      const deletedItemIndex = questions.value.findIndex(
        (item) => item.id === id
      );
      if (deletedItemIndex > -1) {
        questions.value.splice(deletedItemIndex, 1);
      }
    }
  };

  return {
    questions,
    isCreating,
    getQuestions,
    createQuestion,
    updateById,
    deleteById,
  };
});
